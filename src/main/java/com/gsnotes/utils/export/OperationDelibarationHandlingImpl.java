package com.gsnotes.utils.export;

import com.gsnotes.bo.*;
import com.gsnotes.bo.Module;
import com.gsnotes.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

@Service
public class OperationDelibarationHandlingImpl implements OperationDeliberation {

    @Autowired
    INiveau niveauDao;

    @Autowired
    IModule module;

    @Autowired
    InsciptionModuleDao insciptionModuleDao;

    @Autowired
    InscriptionAnnuelDao inscriptionAnnuelDao;

    @Autowired
    InscriptionElementsDao inscriptionElementsDao;

    @Autowired
    IUtilisateurDao iUtilisateurDao;

    Niveau niveau;
    List<Module> Modules=new ArrayList<>();
    List<Etudiant> etudiants=new ArrayList<>();
    List<Long> etudiantsId=new ArrayList<>();

    public void Processing(Long niveau,int annee,notesEtudiantExporter notesEtudiantExporter){

        this.niveau=niveauDao.getById(niveau);
        this.Modules=module.listModules(Long.valueOf(niveau));
        if (this.niveau.getFiliere().getIdFiliere()!=null && this.niveau.getFiliere().getIdFiliere()==0){
            notesEtudiantExporter.CylcleIng=false;
        }

//        System.out.println(niveauDao.getById(niveau).getTitre());
//        String niveauTitre="";
//        Niveau niv=niveauDao.getById(niveau);
//        if(niveauDao.getById(niveau)!=null){
//            niveauTitre=niveauDao.getById(niveau).getTitre();
//        }
        List<InscriptionAnnuelle> list =inscriptionAnnuelDao.etdInOrd(Long.valueOf(niveau),annee);
        TreeSet<InscriptionAnnuelle> treeSet=new TreeSet<>(list);

        etudiants=listEtudiants(treeSet);
        etudiantsId=EtudiantInscriptionId(treeSet);

//        notesEtudiantExporter.niveau=niveauTitre;
        notesEtudiantExporter.setColomNbre(etudiants.size());
        notesEtudiantExporter.SubHeader();
        notesEtudiantExporter.insertInFile(0,fillListWihEtd(etudiants));
        notesEtudiantExporter.listModulesDesc(Modules);
        notesEtudiantExporter.ModulesDetails();
        DatainExcel(notesEtudiantExporter);
        notesEtudiantExporter.ColoumMoyenneGeneral(notesEtudiantExporter.getColoumCursor());
        notesEtudiantExporter.RankColoum(notesEtudiantExporter.getColoumCursor()+1);
    }

//    public void niveau(Long niveau) {
//        this.niveau = niveau;
//    }

    public void DatainExcel(notesEtudiantExporter notesExporter)  {

        int c=notesExporter.StartBodyDataAtCol;
        for (Module m:Modules){
            Long idModule=m.getIdModule();
            if(!m.getElements().isEmpty()){
                List<List<Object>> data=notesElements(etudiantsId,m.getElements());
                notesExporter.insertInFile(c,data);
                c+=m.getElements().size();
            }
            List<Object> objects=notesModuleFinal(etudiantsId,idModule);
            notesExporter.CellwithFormule(c,objects);
            c+=2;
        }
    }

    public List<List<Object>> fillListWihEtd(List<Etudiant> etudiants){
        List<List<Object>> listEtudiant=new ArrayList<>();
        List<Object> list;
        for (Etudiant etd : etudiants){
            list=new ArrayList<>();

            list.add(etd.getIdUtilisateur());
            list.add(etd.getCne());
            list.add(etd.getNom());
            list.add(etd.getPrenom());

            listEtudiant.add(list);
        }
        return listEtudiant;
    }

    public List<Etudiant> listEtudiants(Collection<InscriptionAnnuelle> annuelleList ){
        List<Etudiant> l=new ArrayList<>();
        for (InscriptionAnnuelle annuelle:annuelleList){
            l.add(annuelle.getEtudiant());
        }return l;
    }

    public List<Long> EtudiantInscriptionId(Collection<InscriptionAnnuelle> annuelleList ){
        List<Long> l=new ArrayList<>();
        for (InscriptionAnnuelle annuelle:annuelleList){
            l.add(annuelle.getIdInscription());
        }return l;
    }

    public List<Object> notesModuleFinal(List<Long> EtdInsAnnuel, Long idModule){
        List<Object> notes=new ArrayList<>();
        for(Long id:EtdInsAnnuel){
            InscriptionModule module=insciptionModuleDao.noteOfEtdutiant(id,idModule);
            if(module==null){
                notes.add("NULL");
            }else {
                notes.add(module.getNoteFinale());
            }
        }
        return notes;
    }

    public List<List<Object>> notesElements(List<Long> EtdInsAnnuel, List<Element> elts){

        List<Long> elementsId=new ArrayList<>();
        for (Element el:elts){
            elementsId.add(el.getIdMatiere());
        }

        List<List<Object>> notes=new ArrayList<>();
        int i=0;
        for (Long el:elementsId){
            int j=0;
            for (Long etd:EtdInsAnnuel){
                List<Object> l=new ArrayList<>();
                InscriptionMatiere matiere=inscriptionElementsDao.notesElementsOfEtd(etd,el);
                if(i>0){
                    if (matiere == null) {
                        notes.get(j++).add("NULL");
                    } else {
                        notes.get(j++).add(matiere.getNoteFinale());
                    }
                }else {
                    if (matiere == null) {
                        l.add("NULL");
                    } else {
                        l.add(matiere.getNoteFinale());
                    }
                    notes.add(l);
                }
            }
            i++;
        }return notes;
    }
}
