package com.taipicala.dao;

import com.taipicala.model.dto.Comprobante;
import com.taipicala.util.JPAUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class ComprobanteDAO implements GenericDAO<Comprobante, Integer> {

    private EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    @Override
    public Comprobante create(Comprobante comprobante) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(comprobante);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return comprobante;
    }

    @Override
    public Comprobante read(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Comprobante.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Comprobante update(Comprobante comprobante) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Comprobante c = em.merge(comprobante);
            em.getTransaction().commit();
            return c;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Comprobante comprobante) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Comprobante c = em.merge(comprobante);
            em.remove(c);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Comprobante> readAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT c FROM Comprobante c LEFT JOIN FETCH c.detalles ORDER BY c.idComprobante DESC", Comprobante.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}

