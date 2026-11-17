package com.taipicala.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {
    private static EntityManagerFactory emf;

    private JPAUtil() {
        // Utility class private constructor
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            try {
                emf = Persistence.createEntityManagerFactory("HotelTaipicalaPU");
            } catch (Exception e) {
                System.err.println("CRITICAL ERROR: Failed to create EntityManagerFactory: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static synchronized void shutdown() {
        if (emf != null && emf.isOpen()) {
            try {
                emf.close();
            } catch (Exception e) {
                System.err.println("Warning closing EntityManagerFactory: " + e.getMessage());
            } finally {
                emf = null;
            }
        }
    }
}

