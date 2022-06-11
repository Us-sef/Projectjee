package com.gsnotes.dao;


import com.gsnotes.bo.InscriptionModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InsciptionModuleDao extends JpaRepository<InscriptionModule,Long> {

    @Query("select module   from InscriptionModule module where module.inscriptionAnnuelle.idInscription=:is " +
            " and module.module.idModule=:im ")
   InscriptionModule noteOfEtdutiant(@Param("is") Long idInscrip , @Param("im") Long idModule);

}
