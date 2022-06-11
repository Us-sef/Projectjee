package com.gsnotes.dao;

import com.gsnotes.bo.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IModule  extends JpaRepository<Module,Long> {

    @Query("select modules from Module modules where modules.niveau.idNiveau=:niv ORDER BY modules.code ,modules.titre ")
    List<Module> listModules(@Param("niv") Long n);
}
