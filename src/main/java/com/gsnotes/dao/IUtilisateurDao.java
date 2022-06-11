package com.gsnotes.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gsnotes.bo.Utilisateur;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IUtilisateurDao extends JpaRepository<Utilisateur, Long> {

	public Utilisateur getUtilisateurByCin(String cin);


}
