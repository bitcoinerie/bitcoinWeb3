package fr.bitcoinerie.service;

import fr.bitcoinerie.domain.MyEchange;
import fr.bitcoinerie.domain.MyTransaction;
import fr.bitcoinerie.domain.MyUser;


import java.util.Date;
import java.util.List;


public interface MyEchangeService {
    //@Transactional
    void saveEchange(MyEchange echange);
    void updateEchange(MyEchange myEchange);

    void deleteEchange(Long id);
     void saveuserandmajEchange(MyUser myUser) ;
    void dotransactionandmajechange(MyTransaction trans);

    List<MyEchange> findAllEchange();
    MyEchange findByIdEchange(Long id) ;

    List<MyEchange> findByEmetteurEchange(Long id);

    List<MyEchange> findByRecepteurEchange(Long id);

    void nouvuser( MyUser nouveau, Double montant);

    MyEchange findOneEchange(Long emet, Long recept) ;

    int countEchange();

    void majEchange(Double montant, Date date_temps, Long emet, Long recept) ;



    void calculproba(Long emet, Long recept);

     void majproba(Long emetteur, Long recepteur);
    void majreput(Double alpha) ;

}
