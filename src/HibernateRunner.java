import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HibernateRunner {
    private List<User> users;
    private HibernateConfig theHibernateUtility;

    public HibernateRunner(){
        theHibernateUtility = new HibernateConfig();
    }

    public static void main(String[] args){
        HibernateRunner aSillyHibernateRunner = new HibernateRunner();
        aSillyHibernateRunner.addNewUsers();
        aSillyHibernateRunner.showAllUsers();
        aSillyHibernateRunner.modifyUser();
//        aSillyHibernateRunner.addSharedPhoneNumber();
//        aSillyHibernateRunner.deleteAddedUsers();
    }

//     show how to add records to the database
    private void addNewUsers() {
        Session session = theHibernateUtility.getCurrentSession();
        Transaction transaction = session.beginTransaction();

//        create some User instances.
//         Nasty path - running this again, to see if it makes duplicate users.
//      Result: no duplicates.
        User aWardUser = new User();
        aWardUser.setUname("derekward");
        aWardUser.setPword("derekspassword");
        User aJuddUser = new User();
        aJuddUser.setUname("damenjudd");
        aJuddUser.setPword("damenspassword");

//         Nasty path - null users and empty users
//        Result: I didn't run into any problems with these, probably because I didn't save them to the session and commit them.
        User aLenzUser = new User();
        aLenzUser.setUname(null);
        aLenzUser.setPword(null);
        User aRutledgeUser = new User();
        aRutledgeUser.setUname("");
        aRutledgeUser.setPword("");

//        save each instance as a record in the database
        session.save(aWardUser);
        session.save(aJuddUser);
//        Nasty path - Not saving the null and empty users - what happens?
//        session.save(aRutledgeUser);
//        session.save(aLenzUser);
        transaction.commit();

//        prove that the User instances were added to the database and that the instances were each updated with a database generated id.
        System.out.println("Derek's generated ID is: " + aWardUser.getId());
        System.out.println("Damen's generated ID is: " + aJuddUser.getId());
    }

//     show how to get a collection of type List containing all of the records in the app_user table
    private void showAllUsers() {
        Session session = theHibernateUtility.getCurrentSession();
        Transaction transaction = session.beginTransaction();

//         execute a HQL query against the database.  HQL is NOT SQL.  It is object based.
        Query allUsersQuery = session.createQuery("select u from User as u order by u.id");

//         get a list of User instances based on what was found in the database tables.
        users = allUsersQuery.list();
        System.out.println("Number of users in the database: " + users.size());

//         iterate over each User instance returned by the query and found in the list.
        Iterator<User> iter = users.iterator();
        while(iter.hasNext()) {
            User element = iter.next();
            System.out.println(element.toString());
            System.out.println("Number of phone numbers: " + element.getPhoneNumbers().size());
        }
        transaction.commit();
    }

//     show how to modify a database record
    private void modifyUser() {

        Session session = theHibernateUtility.getCurrentSession();
        Transaction transaction = session.beginTransaction();

//         get a single User instance from the database.
        Query singleUserQuery = session.createQuery("select u from User as u where u.uname='derekward'");

//        Querying a nonexistent username. Result: NullPointerException
//        Query singleUserQuery = session.createQuery("select u from User as u where u.uname='nonexistentuser'");

//        // nasty path - null query. Result: NullPointerException
//        Query aNullQuery = session.createQuery(null);

//        // nasty path - empty query. Result: Unexpected end of subtree[]
//        Query anEmptyQuery = session.createQuery("");

//        // nasty path - malformed query. Result: Unexpected token: hello, IllegalArgumentException: node to traverse cannot be null!
//        Query anMalformedQuery = session.createQuery("hello world");

//        // nasty path - SQL instead of HQL query. Result: UndeclaredThrowableException, InvocationTargetException, NoClassDefFoundError: user (wrong name: User)
//        Query anotherMalformedQuery = session.createQuery("select * from user");

//        // nasty path - Class type that doesn't exist. Result: QuerySyntaxException: Bacon is not mapped [select u from Bacon...]
//        Query classTypeQuery = session.createQuery("select u from Bacon as b where b.happinessLevel='transcendent'");

        User aWardUser = (User)singleUserQuery.uniqueResult();

//         change the user name for the Java instance
        aWardUser.setUname("jordanward");

//         call the session merge method for the User instance in question.  This tells the database that the instance is ready to be permanently stored.
        session.merge(aWardUser);

//         call the transaction commit method.  This tells the database that the changes are ready to be permanently stored.
        transaction.commit();

//         permanently store the changes into the database tables.
        showAllUsers();
    }

    private void addSharedPhoneNumber() {
        Session session = theHibernateUtility.getCurrentSession();
        Transaction transaction = session.beginTransaction();

//         get two User instances from the database using HQL.  This is NOT SQL.  It is object based.
        Query joshuaQuery = session.createQuery("select u from User as u where u.uname='Joshua'");
        User joshuaUser = (User)joshuaQuery.uniqueResult();
        Query aNameQuery = session.createQuery("select u from User as u where u.uname='aName'");
        User aNameUser = (User)aNameQuery.uniqueResult();

//         create a PhoneNumber instance
        PhoneNumber sharedPhoneNumber = new PhoneNumber();
        sharedPhoneNumber.setPhone("(546)222-9898");

//        add the shared phone number to the joshuaUser
        Set<PhoneNumber> joshuaPhoneNumbers = joshuaUser.getPhoneNumbers();
        joshuaPhoneNumbers.add(sharedPhoneNumber);

//        set the single phone number to be used by more than one User
        Set<PhoneNumber> aNamePhoneNumbers = aNameUser.getPhoneNumbers();
        aNamePhoneNumbers.add(sharedPhoneNumber);

//        inform the database that the phone number should be ready for permanent storage.
        session.save(sharedPhoneNumber);

//        inform the database that the modified User instances should be ready for permanent storage.
        session.merge(joshuaUser);
        session.merge(aNameUser);

//        permanently store the changes into the database tables.
        transaction.commit();

//        show that the database was updated by printing out all of the User instances created by a HQL query
        showAllUsers();
    }
    private void deleteAddedUsers() {
        Session session = theHibernateUtility.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        int numUsers = users.size();
        System.out.println("user count: " + numUsers);
        for(int i = 0; i < numUsers; i++){
            System.out.println("deleting user " + users.get(i).getUname());
            session.delete(users.get(i));
        }
        transaction.commit();

          /* at this point the records have been removed from the database but still exist in our class list attribute.
          * Do not store lists retrieved from the database since they will be out of sync with the database table from which they come.
          * This example shows that you should not store retrieved lists.
          */
        System.out.println(users);
        users.clear();

//          now the Java instances are also gone and the database is back to its original state so the example application can be run again.
        System.out.println(users);
    }
}
