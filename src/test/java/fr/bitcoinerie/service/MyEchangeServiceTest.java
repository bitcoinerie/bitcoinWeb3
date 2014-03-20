package fr.bitcoinerie.service;

import fr.bitcoinerie.domain.MyEchange;
import fr.bitcoinerie.domain.MyUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Date;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MyEchangeServiceTest {

    @Inject
    private SessionFactory sessionFactory;

    @Inject
    private MyEchangeService myEchangeService;
    @Inject
    private MyUserService myUserService;

    private MyUser Paul;
    private MyUser Jean;
    private Date dateTest = new Date();

    @After
    public void cleanDb() {


        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        session.createQuery("delete from MyEchange").executeUpdate();

        transaction.commit();
        Transaction transaction2 = session.beginTransaction();

        session.createQuery("delete from MyUser").executeUpdate();

        transaction2.commit();

        session.close();


    }


   @Test
    public void testSave() throws Exception {
        System.out.println("1 >>>>>>>>");
        myEchangeService.saveEchange( new MyEchange( 100., Paul , Jean));
       Session session = sessionFactory.openSession();
       Assert.assertEquals(1, session.createQuery("from MyEchange").list().size());
       session.close();


    }



    @Test
    public void testDelete() throws Exception {

        MyEchange myEchange = new MyEchange( 200., Paul , Jean);

        myEchangeService.saveEchange(myEchange);

        myEchangeService.deleteEchange(myEchange.getId_echange());

        Session session = sessionFactory.openSession();

        Assert.assertEquals(0, session.createQuery("from MyEchange").list().size());

        session.close();

    }

    @Test
    public void testFindAll() throws Exception {


        myEchangeService.saveEchange( new MyEchange( 230., Paul , Jean));
        myEchangeService.saveEchange( new MyEchange( 400., Paul , Paul));

        Assert.assertEquals(2, myEchangeService.findAllEchange().size());

    }

    @Test
    public void testFindByEmetteur() throws Exception {
        Jean =  new MyUser("Jean", "Kevin", 100);
        Paul =  new MyUser("Paul", "Hidalgo", 200);

        myEchangeService.saveuserandmajEchange(Jean);
        myEchangeService.saveuserandmajEchange(Paul);
        Long id_paul=  Paul.getId_user();
        Long id_jean= Jean.getId_user();
        MyEchange echange = myEchangeService.findOneEchange(id_paul , id_jean);
        echange.setMontant(300.);
        myEchangeService.updateEchange(echange);

        MyEchange echange2 = myEchangeService.findOneEchange(id_paul , id_jean);
        echange.setMontant(450.);
        myEchangeService.updateEchange(echange2);




        Assert.assertEquals(2, myEchangeService.findByEmetteurEchange(id_paul ).size());
        Assert.assertEquals(2, myEchangeService.findByEmetteurEchange(id_jean ).size());

    }
    @Test
    public void testfindOneEchange(){
        Jean =  new MyUser("Jean", "Kevin", 100);
        Paul =  new MyUser("Paul", "Hidalgo", 200);

        myEchangeService.saveuserandmajEchange(Jean);
        myEchangeService.saveuserandmajEchange(Paul);

        Long id_paul=  Paul.getId_user();
        Long id_jean= Jean.getId_user();
        MyEchange echange = myEchangeService.findOneEchange(id_paul , id_jean);
        echange.setMontant(300.);
        myEchangeService.updateEchange(echange);

        MyEchange echange2 = myEchangeService.findOneEchange(id_paul , id_paul);
        echange.setMontant(450.);
        myEchangeService.updateEchange(echange2);
        Assert.assertEquals(300.F, myEchangeService.findOneEchange(id_paul, id_jean).getMontant(),0.F);

    }

    @Test
    public void testfindByIdEchange(){
        Jean =  new MyUser("Jean", "Kevin", 100);
        Paul =  new MyUser("Paul", "Hidalgo", 200);
        myEchangeService.saveuserandmajEchange(Jean);
        myEchangeService.saveuserandmajEchange(Paul);
        Long id_paul=  Paul.getId_user();
        Long id_jean= Jean.getId_user();
        MyEchange echange = myEchangeService.findOneEchange(id_paul , id_jean);
        echange.setMontant(300.);
        myEchangeService.updateEchange(echange);



        Long id_echange =myEchangeService.findOneEchange(id_paul,id_jean).getId_echange();
        Assert.assertEquals(300.F, myEchangeService.findByIdEchange(id_echange).getMontant(),0.F);
    }
    @Test
    public void testmajEchange(){
        Jean =  new MyUser("Jean", "Kevin", 100);
        Paul =  new MyUser("Paul", "Hidalgo", 200);

        myEchangeService.saveuserandmajEchange(Jean);
        myEchangeService.saveuserandmajEchange(Paul);
        myEchangeService.saveEchange( new MyEchange( 0., Paul , Jean));
        myEchangeService.saveEchange( new MyEchange( 0., Jean , Paul));

        Long id_jean =Jean.getId_user();
        Long id_paul =Paul.getId_user();
        myEchangeService.majEchange(100., dateTest,id_paul, id_jean) ;

        Assert.assertEquals(100.F, myEchangeService.findOneEchange(id_paul, id_jean ).getMontant(),0.F);
        Assert.assertEquals(100.F, myEchangeService.findOneEchange(id_paul, id_paul ).getMontant(),0.F);
        Assert.assertEquals(200.F, myEchangeService.findOneEchange(id_jean, id_jean ).getMontant(),0.F);

    }
   @Test
     public void testmajproba(){
        Jean =  new MyUser("Jean", "Kevin", 100);
        Paul =  new MyUser("Paul", "Hidalgo", 200);

       myEchangeService.saveuserandmajEchange(Jean);
       myEchangeService.saveuserandmajEchange(Paul);


        Long id_jean =Jean.getId_user();
        Long id_paul =Paul.getId_user();
        myEchangeService.majEchange(100., dateTest,id_paul, id_jean) ;
        myEchangeService.majproba(id_paul, id_jean) ;

        Assert.assertEquals(0.5, myEchangeService.findOneEchange(id_paul, id_jean ).getProbabilite(),0.F);
        Assert.assertEquals(0.5, myEchangeService.findOneEchange(id_paul, id_paul ).getProbabilite(),0.F);
        Assert.assertEquals(1.F, myEchangeService.findOneEchange(id_jean, id_jean ).getProbabilite(),0.F);

    }
    @Test
    public void testcalculproba(){
        Jean =  new MyUser("Jean", "Kevin", 100);
        Paul =  new MyUser("Paul", "Hidalgo", 200);

        myEchangeService.saveuserandmajEchange(Jean);
        myEchangeService.saveuserandmajEchange(Paul);

        Long id_jean =Jean.getId_user();
        Long id_paul =Paul.getId_user();

        myEchangeService.majEchange(100., dateTest,id_paul, id_jean) ;


        myEchangeService.calculproba(id_paul, id_jean) ;

        Assert.assertEquals(0.5, myEchangeService.findOneEchange(id_paul, id_jean ).getProbabilite(),0.F);


    }
   @Test
    public void testupdateEchange(){
        Jean =  new MyUser("Jean", "Kevin", 100);
        Paul =  new MyUser("Paul", "Hidalgo", 200);

       myEchangeService.saveuserandmajEchange(Jean);
       myEchangeService.saveuserandmajEchange(Paul);
        Long id_jean =Jean.getId_user();
        Long id_paul =Paul.getId_user();



         MyEchange echange = myEchangeService.findOneEchange(id_paul , id_jean);
        echange.setMontant(200.);
        myEchangeService.updateEchange(echange);

        Assert.assertEquals(200, myEchangeService.findOneEchange(id_paul , id_jean).getMontant(), 0.F);
        Assert.assertEquals(4, myEchangeService.findAllEchange().size());

    }
    @Test
    public void testnouvuser(){
        Jean =  new MyUser("Jean", "Kevin", 100);
        myEchangeService.saveuserandmajEchange(Jean);
        myEchangeService.nouvuser(dateTest, Jean,100.);
        Paul =  new MyUser("Paul", "Hidalgo", 200);
        myEchangeService.saveuserandmajEchange(Paul);
        myEchangeService.nouvuser(dateTest, Paul,200.);
        Long id_jean =Jean.getId_user();
        Long id_paul =Paul.getId_user();

        Assert.assertEquals(0.F, myEchangeService.findOneEchange(id_paul, id_jean ).getMontant(),0.F);
        Assert.assertEquals(200.F, myEchangeService.findOneEchange(id_paul, id_paul ).getMontant(),0.F);
        Assert.assertEquals(100.F, myEchangeService.findOneEchange(id_jean, id_jean ).getMontant(),0.F);

    }
    @Test
    public void testmajreput(){

        Jean =  new MyUser("Jean", "Kevin", 100);
        Paul =  new MyUser("Paul", "Hidalgo", 200);
        Assert.assertEquals( (Double)0.,Jean.getReputation());
        myEchangeService.saveuserandmajEchange(Jean);

        Assert.assertEquals(1, myUserService.findAll().size());


        Assert.assertEquals(1, myEchangeService.findAllEchange().size());
        myEchangeService.saveuserandmajEchange(Paul);
        Assert.assertEquals(2, myUserService.findAll().size());


        Assert.assertEquals(4, myEchangeService.findAllEchange().size());
        Long id_jean =Jean.getId_user();
        Long id_paul =Paul.getId_user();
        myEchangeService.majEchange(100., dateTest,id_paul, id_jean) ;
        myEchangeService.majproba(id_paul, id_jean) ;
        Double alpha ;
        alpha= 0.15;
        myEchangeService.majreput(alpha) ;
        Double rep=myUserService.findByQuery("Jean").get(0).getReputation();

        Assert.assertEquals((Double)0.15, rep);

        myEchangeService.majreput(alpha) ;
        Double rep2=myUserService.findByQuery("Jean").get(0).getReputation();

        Assert.assertEquals((Double)0.15, rep2);

    }
}
