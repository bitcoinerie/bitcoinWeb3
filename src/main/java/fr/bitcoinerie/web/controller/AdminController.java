package fr.bitcoinerie.web.controller;

import fr.bitcoinerie.domain.MyEchange;
import fr.bitcoinerie.domain.MyTransaction;
import fr.bitcoinerie.domain.MyUser;
import fr.bitcoinerie.service.MyTransactionService;
import fr.bitcoinerie.service.MyEchangeService;
import fr.bitcoinerie.service.MyUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;


@Controller
public class AdminController {

    @Inject
    private MyTransactionService myTransactionService;

    @Inject
    private MyEchangeService myEchangeService;

    @Inject
    private MyUserService myUserService;

    private MyUser myEmet;
    private MyUser myRecept;

    @ModelAttribute("user")
    private MyUser getUser(){
        return new MyUser();
    }

    @RequestMapping("/addTransaction/add")
    public String addTransactionToViews(Model model) {

        MyTransaction myTransaction = new MyTransaction();

        List<MyUser> emetteur = myUserService.findByQuery("Arnold");
        List<MyUser> recepteur = myUserService.findByQuery("Fabien");

        myEmet = emetteur.get(0);
        myRecept = recepteur.get(0);

        //myTransaction.setEmetteur(myEmet);
        //myTransaction.setRecepteur(myRecept);

        model.addAttribute("myTransac", myTransaction);
        model.addAttribute("myEmetteur",myEmet );
        model.addAttribute("myRecepteur", myRecept );

        return "transactionHistoric";
    }

    @RequestMapping(value = "/transactionHistoric", method = RequestMethod.POST)
    public String post(@ModelAttribute MyTransaction myTrans, BindingResult result) {

        if (result.hasErrors()) {
            return "transactionHistoric";
        }

        myTrans.setDate_temps(new Date());

        List<MyUser> emetteur = myUserService.findByQueryWithRelations(myEmet.getPrenom());
        List<MyUser> recepteur = myUserService.findByQueryWithRelations(myRecept.getPrenom());

        myTrans.setEmetteur(emetteur.get(0));
        myTrans.setRecepteur(recepteur.get(0));

        myUserService.doTransaction(myTrans);


        // Doit faire un updateUser au lieu d'un save      sur  myUserService
        //myUserService.updateUser(myEmet);
        //myUserService.updateUser(myRecept);
        Long id_emet= emetteur.get(0).getId_user();
        Long id_recep= recepteur.get(0).getId_user();

        myTransactionService.saveTransaction(myTrans);


        return "redirect:/transactionHistoric";
    }


    @RequestMapping("/transactionHistoric/{id_transaction}")
    public String edit(@PathVariable Long id_transaction, Model model) {
        model.addAttribute("myTransac", myTransactionService.findByIdTransaction(id_transaction));

        return "transactionHistoric";
    }

    @RequestMapping("/new_user")
    public String addUserToView(Model model) {
        // on injecte un utilisateur vierge dans le modèle
        model.addAttribute("user", new MyUser());

        return "new_user";
    }

    @RequestMapping(value = "/new_user", method = RequestMethod.POST)
    public String post(@ModelAttribute("user") @Valid MyUser user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "new_user";
        }

        myUserService.save(user);


        redirectAttributes.addFlashAttribute("user", user);
        redirectAttributes.addFlashAttribute("flashMessage", "User " + user.getLogin() + " created");


        return "redirect:/home";
    }

    @RequestMapping("/sign_in")
    public String add(Model model) {

        MyUser myuser = new MyUser();
        myuser.setPrenom("Total");
        myuser.setNom("Inconnu");
        myuser.setEmail("totalinconnu@live.fr");

        model.addAttribute("user", myuser);

        return "sign_in";
    }


    @RequestMapping(value = "/sign_in", method = RequestMethod.POST)
    public String signIn(@ModelAttribute("user") @Valid MyUser user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "sign_in";
        }
        MyUser myUser = myUserService.signIn(user.getLogin(), user.getMot_de_passe());

        if (myUser == null) {
            redirectAttributes.addFlashAttribute("flashMessage", "Login ou mot de passe incorrect ! Veuillez réessayer, ou bien créez un compte.");
            return "redirect:/sign_in";
        }
        redirectAttributes.addFlashAttribute("user", myUser);
        redirectAttributes.addFlashAttribute("transactions", myTransactionService.findByUser(myUser.getId_user()));
        redirectAttributes.addFlashAttribute("flashMessage", "Connexion réussie");

        return "redirect:/home";
    }
    @RequestMapping("/home/{id_user}")
    public String redirectHome(@PathVariable Long id_user, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("user", myUserService.findUserById(id_user));
        redirectAttributes.addFlashAttribute("transactions", myTransactionService.findByUser(id_user));

        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home() {
        return "home";
    }

    @RequestMapping("/profile/{id_user}")
    public String redirectProfile(@PathVariable Long id_user, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("user", myUserService.findUserById(id_user));
        redirectAttributes.addFlashAttribute("transactions", myTransactionService.findByUser(id_user));

        return "redirect:/profile";
    }

    @RequestMapping("/profile")
    public String profile() {
        return "profile";
    }

    @RequestMapping("/histo/{id_user}")
    public String redirectHisto(@PathVariable Long id_user, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("user", myUserService.findUserById(id_user));
        redirectAttributes.addFlashAttribute("transactions", myTransactionService.findByUser(id_user));

        return "redirect:/mon_historique";
    }

    @RequestMapping("/mon_historique")
    public String mon_historique() {
        return "transactionHistoric";
    }


    @RequestMapping("/searchUser")
    public String searchUser(Long id_user, String prenom_dest, String nom_dest, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("user", myUserService.findUserById(id_user));
        redirectAttributes.addFlashAttribute("transactions", myTransactionService.findByUser(id_user));

        MyUser user2 = myUserService.findUserByPrenomAndNom(prenom_dest, nom_dest);

        if (user2 == null) {
            redirectAttributes.addFlashAttribute("flashMessage2", "Cet utilisateur n'existe pas");
        }


        redirectAttributes.addFlashAttribute("user2", user2);


        return "redirect:/home";
    }

    @RequestMapping("/newTrans")
    public String newTrans(Long id_user1, Long id_user2, double montant, RedirectAttributes redirectAttributes) {

        MyUser user = myUserService.findUserById(id_user1);
        MyUser user2 = myUserService.findUserById(id_user2);
        MyTransaction trans = new MyTransaction(montant, new Date(), user, user2);
        myTransactionService.saveTransaction(trans);
        myUserService.doTransaction(trans);
        redirectAttributes.addFlashAttribute("user", user);
        redirectAttributes.addFlashAttribute("transactions", myTransactionService.findByUser(id_user1));
        redirectAttributes.addFlashAttribute("flashMessage", "Transaction effectuée !");
        return "redirect:/home";
    }

}
