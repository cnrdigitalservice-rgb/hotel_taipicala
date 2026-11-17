package com.taipicala.dao;

import com.taipicala.model.dto.Pago;
import com.taipicala.util.JPAUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class PagoDAO implements GenericDAO<Pago, Integer> {

    private EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    @Override
    public Pago create(Pago pago) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(pago);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return pago;
    }

    @Override
    public Pago read(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Pago.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Pago update(Pago pago) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Pago p = em.merge(pago);
            em.getTransaction().commit();
            return p;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Pago pago) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Pago p = em.merge(pago);
            em.remove(p);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Pago> readAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Pago p ORDER BY p.idPago DESC", Pago.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}

