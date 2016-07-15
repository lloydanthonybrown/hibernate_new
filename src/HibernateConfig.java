import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateConfig{
	private SessionFactory sessionFactory;

    public HibernateConfig() {
        Configuration config = new Configuration();
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");

        config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/test");
//        Nasty path - different port or database
//        Result: Could no open connection - ERROR: Communications link failure
//        config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:4408/test");

        config.setProperty("hibernate.connection.username", "bobtheghost"); //bobtheghost
        config.setProperty("hibernate.connection.password", "bobtheghostrules"); //bobtheghostrules
//        Nasty path - different username or password (non-existent).
//      Result: Access Denied for this user
//        config.setProperty("hibernate.connection.username", "george"); //bobtheghost
//        config.setProperty("hibernate.connection.password", "bobtheghostrules"); //bobtheghostrules

        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.connection.autocommit", "true");
        config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");

		/*
		 * un-comment the next line of code if you want to be able to drop and recreate tables for your data classes listed below.  This is generally a bad idea for security reasons.
		 */
        //config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        config.setProperty("hibernate.show_sql", "true");
        config.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
        config.setProperty("hibernate.current_session_context_class", "thread");

		 /*  Add your classes here that you want to match your database tables
		 *  The example has a User and a PhoneNumber class.
		 */
        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(PhoneNumber.class);

         /*Hibernate 4.3 - 5.x */ //ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
        /*Hibernate 3.x - 4.2*/ ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();

        sessionFactory = config.buildSessionFactory(serviceRegistry);
    }

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
