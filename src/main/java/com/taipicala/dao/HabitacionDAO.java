package com.taipicala.dao;

import com.taipicala.model.dto.Habitacion;
import com.taipicala.util.JPAUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class HabitacionDAO implements GenericDAO<Habitacion, Integer> {
    
    private EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    @Override
    public Habitacion create(Habitacion habitacion) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(habitacion);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return habitacion;
    }

    @Override
    public Habitacion read(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Habitacion.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Habitacion update(Habitacion habitacion) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Habitacion h = em.merge(habitacion);
            em.getTransaction().commit();
            return h;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Habitacion habitacion) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Habitacion h = em.merge(habitacion);
            em.remove(h);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Habitacion> readAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT h FROM Habitacion h", Habitacion.class).getResultList();
        } finally {
            em.close();
        }
    }
}
