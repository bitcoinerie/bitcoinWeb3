package fr.bitcoinerie.service;

import fr.bitcoinerie.domain.MyEchange;
import fr.bitcoinerie.domain.MyUser;
import fr.bitcoinerie.domain.MyTransaction;
import org.hibernate.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Service
public class MyEchangeServiceImpl implements MyEchangeService {
    @Inject
    private SessionFactory sessionFactory;
    @Inject
    private MyUserService myUserService;


    @Override
    @Transactional
    public void saveEchange(MyEchange echange) {
        Session session = sessionFactory.getCurrentSession();

        session.save(echange);
    }
    @Override
    @Transactional
    public void saveuserandmajEchange(MyUser myUser) {
        Session session = sessionFactory.getCurrentSession();

        session.save(myUser);
        nouvuser(myUser,myUser.getMontant_compte());

    }
    @Override
    @Transactional
    public void dotransactionandmajechange(MyTransaction trans){
       majEchange(trans.getMontant(),new Date(),trans.getEmetteur().getId_user(),trans.getRecepteur().getId_user());
        majproba(trans.getEmetteur().getId_user(),trans.getRecepteur().getId_user());
        majreput(0.15);
    }

    @Override
    @Transactional
    public void deleteEchange(Long id) {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("delete from MyEchange where id_echange = :id");
        query.setLong("id", id);

        query.executeUpdate();

    }

    @Override
    @Transactional
    public MyEchange findByIdEchange(Long id) {
        Session session = sessionFactory.getCurrentSession();



        Query query = session.createQuery("from MyEchange where id_echange =:id");
        query.setLong("id", id);



        MyEchange myEchange =  (MyEchange) query.list().get(0);

        return myEchange;


    }

    @Override
    @Transactional(readOnly = true)
    public List<MyEchange> findAllEchange() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(" from MyEchange ");

        List<MyEchange> myEchanges= query.list();


        return myEchanges;
    }

    @Transactional
    @Override
    public List<MyEchange> findByEmetteurEchange(Long id) {
        Session session = sessionFactory.getCurrentSession();

        //Criteria criteria = session.createCriteria(MyTransaction.class);

        //criteria.add(Restrictions.ilike("emetteur", query, MatchMode.ANYWHERE));

        Query query = session.createQuery("from MyEchange where id_user_emetteur =:id");
        query.setLong("id", id);

        //System.out.println(query.list().size());

        List<MyEchange> myEchanges =   query.list();

        return myEchanges;
    }
        @Transactional
        @Override
        public List<MyEchange> findByRecepteurEchange(Long id) {
            Session session = sessionFactory.getCurrentSession();



            Query query = session.createQuery("from MyEchange where id_user_recepteur =:id");
            query.setLong("id", id);

            //System.out.println(query.list().size());

            List<MyEchange> myEchanges =   query.list();

            return myEchanges;
        }
    @Transactional
    @Override
    public MyEchange findOneEchange (Long emet,Long recept){
        MyEchange echange = null;

        Session session = sessionFactory.getCurrentSession();
       /* Criteria criteria = session.createCriteria(MyEchange.class);
        criteria.add(Restrictions.and(Restrictions.eq("id_user_emetteur", emet),
                Restrictions.eq("id_user_recepteur", recept)));

        List<MyEchange> myEchanges = criteria.list();
        MyEchange echange = myEchanges.get(0) ;   */
        Query query = session.createQuery("from MyEchange where (id_user_recepteur =:recept)and(id_user_emetteur =:emet)");
        query.setLong("recept", recept);
        query.setLong("emet", emet);
        List<MyEchange> myEchanges =   query.list();
        if(myEchanges.size()!=0){
         echange = myEchanges.get(0);
        }
        return echange;

    }
    @Transactional
    @Override
    public void majEchange (Double montant, Date date_temps, Long emet, Long recept) {

        MyEchange echange=findOneEchange(emet,recept );
        if ( echange!=null) {
            echange.setDate_derniere_modification(date_temps);
            echange.setMontant(echange.getMontant() + montant);
            updateEchange(echange);
            MyEchange echangeemet = findOneEchange(emet, emet);
            echangeemet.setDate_derniere_modification(date_temps);
            echangeemet.setMontant(echangeemet.getMontant() - montant);
            updateEchange(echangeemet);
            MyEchange echangerecep = findOneEchange(recept, recept);
            echangerecep.setDate_derniere_modification(date_temps);
            echangerecep.setMontant(echangerecep.getMontant() + montant);
            updateEchange(echangerecep);
        }



    }
    @Transactional
    @Override
   public void nouvuser( MyUser nouveau, Double montant){
        List<MyUser> users= myUserService.findAll();
        int i;
        double size = users.size();
        nouveau.setReputation(1./size);
        myUserService.updateUser(nouveau);
        MyEchange echange3 = new MyEchange( montant, nouveau,nouveau);
        updateEchange(echange3);
        Long idnouv= nouveau.getId_user();
        for (i=0; i< users.size();i++){
            Long id= users.get(i).getId_user();
            if (id!=idnouv) {
                MyEchange ech = findOneEchange(id, idnouv);
                users.get(i).setReputation(users.get(i).getReputation() * (size - 1) / size);
                myUserService.updateUser(users.get(i));
                if (ech == null) {
                    saveEchange(new MyEchange(0., users.get(i), nouveau));
                }
                MyEchange echange2 = findOneEchange(idnouv, id);
                if (echange2 == null) {

                    saveEchange(new MyEchange(0., nouveau, users.get(i)));
                }
            }
        }
        List<MyUser> users2= myUserService.findAll();
                for (i = 0; i < users2.size(); i++) {
                    if (users2.size() > 0) {
                        Long id2 = users2.get(i).getId_user();
                        majproba(id2, idnouv);
                        majproba(idnouv, id2);
                    }

                }

    }

    @Transactional
    @Override
    public void majproba(Long emet, Long recept){


       calculproba( emet, recept);

       calculproba(emet , emet);
        List<MyEchange> echanges= findByEmetteurEchange(recept);
        int i;
        for (i=0; i< echanges.size();i++){
            Long id = echanges.get(i).getRecepteur().getId_user();
            calculproba(recept,id);
        }

    }

    @Transactional
    @Override
    public void majreput(Double alpha){
        List<MyUser> users= myUserService.findAll();
        int i;
        int j;
        for (i=0; i< users.size();i++)
        {
            Double reputvoisins=0.;
            Double proba;
            Double miseajour;
            Long id=users.get(i).getId_user();
            List<MyEchange> echanges= findByRecepteurEchange(id);

            for (j=0;j< echanges.size();j++)
            {

                MyUser myUser=echanges.get(j).getEmetteur();
                if (myUser.getId_user()!= id) {
                Double reputvoisin = myUser.getReputation();
                proba = echanges.get(j).getProbabilite();

                miseajour = proba * reputvoisin;
                reputvoisins = reputvoisins + miseajour;
            }
            }
            List<MyEchange> echanges2= findByEmetteurEchange(id);
            for (j=0;j< echanges2.size();j++)
            {

                MyUser myUser2=echanges2.get(j).getEmetteur();
                if (myUser2.getId_user()!= id) {
                    Double reputvoisin = myUser2.getReputation();
                    proba = echanges.get(j).getProbabilite();

                    miseajour = proba * reputvoisin;
                    reputvoisins = reputvoisins + miseajour;
                }
            }
             Double reput=(alpha/users.size())+(1-alpha)*0.5*reputvoisins ;

              users.get(i).setReputation(reput);

            myUserService.updateUser(users.get(i));


        }

    }
    @Transactional
    @Override
    public void calculproba(Long emet, Long recept){
        int i;
        Double s=0.;
        MyEchange ech= findOneEchange(emet,recept);
        if (ech != null) {
            List<MyEchange> echanges = findByEmetteurEchange(emet);
            for (i = 0; i < echanges.size(); i++) {
                s = s + ((echanges.get(i)).getMontant());
            }
            if ((s > 0) & (echanges.size() > 0)) {
                Double proba = ech.getMontant() / s;
                ech.setProbabilite(proba);
            }

            Session session = sessionFactory.getCurrentSession();
            updateEchange(ech);
        }
    }



    @Transactional
    @Override
    public int countEchange() {
        // TODO
        return findAllEchange().size();
    }



    @Override
    @Transactional
    public void updateEchange(MyEchange myEchange) {
        Session session = sessionFactory.getCurrentSession();

                    session.saveOrUpdate(myEchange);




        }

}