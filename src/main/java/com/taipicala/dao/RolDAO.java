package com.taipicala.dao;

import com.taipicala.model.dto.Rol;
import com.taipicala.util.JPAUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class RolDAO implements GenericDAO<Rol, Integer> {
    
    private EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    @Override
    public Rol create(Rol rol) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(rol);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return rol;
    }

    @Override
    public Rol read(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Rol.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Rol update(Rol rol) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            rol = em.merge(rol);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return rol;
    }

    @Override
    public void delete(Rol rol) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Reattach before removing
            Rol mergedRol = em.merge(rol);
            em.remove(mergedRol);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Rol> readAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Rol r", Rol.class).getResultList();
        } finally {
            em.close();
        }
    }
}
