package com.taipicala.dao;

import com.taipicala.model.dto.Cliente;
import com.taipicala.util.JPAUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class ClienteDAO implements GenericDAO<Cliente, Integer> {
    
    private EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    @Override
    public Cliente create(Cliente cliente) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return cliente;
    }

    @Override
    public Cliente read(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Cliente update(Cliente cliente) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Cliente c = em.merge(cliente);
            em.getTransaction().commit();
            return c;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Cliente cliente) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Cliente c = em.merge(cliente);
            em.remove(c);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Cliente> readAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public Cliente findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Cliente> list = em.createQuery("SELECT c FROM Cliente c WHERE c.email = :email", Cliente.class)
                                   .setParameter("email", email)
                                   .getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }
}
