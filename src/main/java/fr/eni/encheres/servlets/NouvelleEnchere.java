package fr.eni.encheres.servlets;

import fr.eni.encheres.BusinessException;
import fr.eni.encheres.bll.ArticleManager;
import fr.eni.encheres.bll.EnchereManager;
import fr.eni.encheres.bll.UtilisateurManager;
import fr.eni.encheres.bo.Articles;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = "/NouvelleEnchere")
public class NouvelleEnchere extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/nouvelleEnchere.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        List<Integer> listeCodesErreur=new ArrayList<>();

        try {

            creerEnchere(request, listeCodesErreur);
            preleverCredit(request, listeCodesErreur);
            rendreCredit(request, listeCodesErreur);
            updatePrixVenteArticle(request, listeCodesErreur);


        } catch (BusinessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if(listeCodesErreur.size() > 0) {
            request.getRequestDispatcher("/WEB-INF/nouvelleEnchere.jsp").forward(request,response);
        } else {
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        }
    }

    private void creerEnchere(HttpServletRequest request, List<Integer> listeCodesErreur) throws BusinessException {
        EnchereManager enchereManager = new EnchereManager();
        Enchere enchere = null;

        Utilisateur utilisateur = lireParametreUtilisateur(request, listeCodesErreur);
        System.out.println(utilisateur);
        Articles article = lireParametreArticle(request, listeCodesErreur);
        LocalDateTime date = lireParametreDate(request, listeCodesErreur);
        int montant = lireParametreMontant(request, listeCodesErreur);

        if (listeCodesErreur.size() > 0) {
            request.setAttribute("listeCodesErreur", listeCodesErreur);
        } else {
            enchere = new Enchere(utilisateur, article, date, montant);
            try {
                enchereManager.ajouterEnchere(enchere);
            } catch (BusinessException ex) {
                ex.printStackTrace();
                request.setAttribute("listeCodesErreur", ex.getListeCodesErreur());
            }
        }
    }

    private void preleverCredit (HttpServletRequest request, List<Integer> listeCodesErreur) throws BusinessException {
        UtilisateurManager utilisateurManager = new UtilisateurManager();
        Utilisateur utilisateur = lireParametreUtilisateur(request, listeCodesErreur);
        int montant = lireParametreMontant(request, listeCodesErreur);
        utilisateur.setCredit(utilisateur.getCredit()-montant);
        utilisateurManager.updateUserWithCheck(utilisateur);
    }

    private void rendreCredit (HttpServletRequest request, List<Integer> listeCodesErreur) throws BusinessException {
        UtilisateurManager utilisateurManager = new UtilisateurManager();
        EnchereManager enchereManager = new EnchereManager();
        ArticleManager articleManager = new ArticleManager();
        Articles article = lireParametreArticle(request, listeCodesErreur);
        Utilisateur utilisateur = null;

        Articles fullArticle = articleManager.selectByNoArticle(article.getNoArticle());
        List<Enchere> listeEnchere = enchereManager.listeEnchereEnCoursParArticle(fullArticle);

        for (Enchere e : listeEnchere) {
            if (e.getMontant_enchere().equals(fullArticle.getPrixVente())){
                utilisateur = utilisateurManager.afficherUnProfil(e.getUtilisateur().getPseudo());
                utilisateur = e.getUtilisateur();
                System.out.println(utilisateur);
                utilisateur.setCredit(utilisateur.getCredit()+e.getMontant_enchere());
                System.out.println(utilisateur);
                utilisateurManager.updateUserWithCheck(utilisateur);
            }
        }
    }

    private void updatePrixVenteArticle (HttpServletRequest request, List<Integer> listeCodesErreur) throws BusinessException {
        ArticleManager articleManager = new ArticleManager();
        Articles article = lireParametreArticle(request, listeCodesErreur);
        Articles fullArticle = articleManager.selectByNoArticle(article.getNoArticle());
        int montant = lireParametreMontant(request, listeCodesErreur);
        fullArticle.setPrixVente(montant);
        System.out.println("test5");
        System.out.println(fullArticle);
        try {
            System.out.println("test6");
            articleManager.updateUnArticle(fullArticle);
        }catch ( BusinessException ex){
            ex.printStackTrace();
        }

    }

    private Utilisateur lireParametreUtilisateur(HttpServletRequest request, List<Integer> listeCodesErreur) {
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if(utilisateur == null) {
            listeCodesErreur.add(CodesResultatServlets.ENCHERE_UTILISATEUR_OBLIGATOIRE);
        }
        return utilisateur;
    }

    private Articles lireParametreArticle(HttpServletRequest request, List<Integer> listeCodesErreur) {
        Articles article = new Articles();
        article.setNoArticle(Integer.valueOf(request.getParameter("noArticle")));
        article.setPrixVente(Integer.valueOf(request.getParameter("prixVente")));
        if (article == null) {
            listeCodesErreur.add(CodesResultatServlets.ENCHERE_ARTICLE_OBLIGATOIRE);
        }
        return article;
    }

    private LocalDateTime lireParametreDate(HttpServletRequest request, List<Integer> listeCodesErreur) {
        LocalDateTime date = LocalDateTime.now();
        if(date == null) {
            listeCodesErreur.add(CodesResultatServlets.ENCHERE_DATE_OBLIGATOIRE);
        }
        return date;
    }

    private int lireParametreMontant(HttpServletRequest request, List<Integer> listeCodesErreur) {
        Articles article = lireParametreArticle(request, listeCodesErreur);
        int montant = Integer.parseInt(request.getParameter("montant"));
        if (montant <= article.getPrixVente()) {
            listeCodesErreur.add(CodesResultatServlets.ENCHERE_MONTANT_OBLIGATOIRE);
        }
        return montant;
    }
}