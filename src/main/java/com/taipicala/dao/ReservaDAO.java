package com.taipicala.dao;

import com.taipicala.model.dto.Reserva;
import com.taipicala.util.JPAUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class ReservaDAO implements GenericDAO<Reserva, Integer> {
    
    private EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    @Override
    public Reserva create(Reserva reserva) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reserva);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return reserva;
    }

    @Override
    public Reserva read(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Reserva.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Reserva update(Reserva reserva) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reserva r = em.merge(reserva);
            em.getTransaction().commit();
            return r;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Reserva reserva) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reserva r = em.merge(reserva);
            em.remove(r);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reserva> readAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Reserva r", Reserva.class).getResultList();
        } finally {
            em.close();
        }
    }
}
