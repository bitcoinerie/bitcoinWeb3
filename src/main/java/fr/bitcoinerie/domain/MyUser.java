package fr.bitcoinerie.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="userTab")
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_user;

    @Column
    @NotBlank
    @Size(min = 1, max = 50)
    private String nom;


    @NotBlank
    @Size(min = 1, max = 50)
    private String prenom;


    @Column
    private Double reputation=0.15;

    @Column
    private Double montant_compte;

    @Column
    //@NotBlank
    @Size(min = 5, max = 16)
    private String login;

    @Column
    @NotBlank
    @Size(min = 8, max = 16)
    private String mot_de_passe;

    @Column
    private Date date_inscription;

    @Column
    private String email;

    @Column
    private String userStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="emetteur")
    private Set<MyTransaction> liste_depenses;

    @OneToMany(fetch = FetchType.LAZY,mappedBy="recepteur")
    private Set<MyTransaction> liste_recettes;

    public MyUser(){
        setDate_inscription(new Date());
        setMontant_compte(10.);
        this.reputation=1.;
        setUserStatus("normal_user");
//        setMot_de_passe("user_created");

        liste_depenses = new HashSet<MyTransaction>();
        liste_recettes = new HashSet<MyTransaction>();
    }

    public MyUser(String prenom, String nom, double montant){
        this.prenom = prenom;
        this.nom = nom;
        this.montant_compte = montant;
        setDate_inscription(new Date());
        this.reputation=1.;
        setUserStatus("normal_user");
        setLogin("user_created");
        setMot_de_passe("user_created");

        liste_depenses = new HashSet<MyTransaction>();
        liste_recettes = new HashSet<MyTransaction>();
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", reputation=" + reputation +
                ", montant_compte=" + montant_compte +
                ", liste_depenses=" + liste_depenses +
                ", liste_recettes=" + liste_recettes +
                '}';
    }

    public Long getId_user() {
        return id_user;
    }

    public void setId_user(Long id_user) {
        this.id_user = id_user;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Double getReputation() {
        return reputation;
    }

    public void setReputation(Double reputation) {
        this.reputation = reputation;
    }

    public Double getMontant_compte() {
        return montant_compte;
    }

    public void setMontant_compte(Double montant_compte) {
        this.montant_compte = montant_compte;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Date getDate_inscription() {
        return date_inscription;
    }

    public void setDate_inscription(Date date_inscription) {
        this.date_inscription = date_inscription;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public Set<MyTransaction> getListe_depenses() {
        return liste_depenses;
    }

    public void setListe_depenses(Set<MyTransaction> liste_depenses) {
        this.liste_depenses = liste_depenses;
    }

    public Set<MyTransaction> getListe_recettes() {
        return liste_recettes;
    }

    public void setListe_recettes(Set<MyTransaction> liste_recettes) {
        this.liste_recettes = liste_recettes;
    }

    public void setMot_de_passe(String mot_de_passe) {this.mot_de_passe = mot_de_passe;}

    public String getMot_de_passe() {return mot_de_passe;}

    public void addRecette(MyTransaction trans){
        liste_recettes.add(trans);
    }

    public void addDepense(MyTransaction trans){
        liste_depenses.add(trans);
    }

    public void addMontant(Double somme){
        setMontant_compte(montant_compte + somme);
    }
}
