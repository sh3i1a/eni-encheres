package fr.eni.encheres.bll;

import fr.eni.encheres.BusinessException;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.dal.DAOFactory;
import fr.eni.encheres.dal.UtilisateurDAO;

import java.sql.SQLException;

public class UtilisateurManager {

    private UtilisateurDAO utilisateurDAO;

    // Constructeur du manager
    public UtilisateurManager() {
        this.utilisateurDAO = DAOFactory.getUtilisateurDAO();
    }

    //Methode ajouter un Utilisateur
    public void ajouterUser(Utilisateur utilisateur) throws BusinessException, SQLException {
        BusinessException businessException = new BusinessException();
        this.validateUser(utilisateur, businessException);

        if(!businessException.hasErreurs()){
            Utilisateur newUtilisateur = new Utilisateur();
            this.utilisateurDAO.insert(newUtilisateur);
        }
    }

    //Methode mise à jour des utilisateurs
    public void updateUser(Utilisateur utilisateur) throws BusinessException, SQLException{
        BusinessException businessException = new BusinessException();
        this.validateUser(utilisateur , businessException);

        if (!businessException.hasErreurs()){
            Utilisateur updateUtilisateur = new Utilisateur();
            this.utilisateurDAO.update(updateUtilisateur);
        }
    }

    //Pouvoir se supprimer
    public void removeUser(Utilisateur utilisateur) throws SQLException {
        utilisateurDAO.delete(utilisateur);
    }

    //afficher son profil
    public Utilisateur afficherSonProfil(String pseudo) throws SQLException{
        return this.utilisateurDAO.selectOwnProfile(pseudo);
    }

    //Afficher un profil en cliquant sur le pseudo d'un utilisateur.
    public Utilisateur afficherUnProfil(String pseudo) throws SQLException{
        return this.utilisateurDAO.selectByPseudo(pseudo);
    }

    //Methode qui valide les données avec insert / update
    public void validateUser(Utilisateur utilisateur, BusinessException businessException) throws SQLException{

        if (utilisateur.getPseudo() == null || utilisateur.getPseudo().isBlank() && !utilisateur.getPseudo().matches("^[a-zA-Z0-9]*$") || utilisateur.getPseudo().length()<8 ){
            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_PSEUDO_ERREUR);
        }

        if(utilisateurDAO.pseudoIsInBase(utilisateur)) {
            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_PSEUDO_IN_BASE_ERREUR);
        }

        if(utilisateur.getNom() == null || utilisateur.getNom().isBlank()){
            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_NOM_ERREUR);
        }

        if (utilisateur.getPrenom() == null || utilisateur.getPrenom().isBlank()){
            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_PRENOM_ERREUR);
        }

        if (utilisateur.getEmail() == null || utilisateur.getEmail().isBlank()){
            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_EMAIL_ERREUR);
        }

        if (utilisateurDAO.emailIsInBase(utilisateur)){
            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_EMAIL_IN_BASE_ERREUR);
        }

        if (utilisateur.getRue() == null || utilisateur.getRue().isBlank()){
            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_RUE_ERREUR);
        }

        if (utilisateur.getCodePostal() == null || utilisateur.getCodePostal().isBlank()){
            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_CODEPOSTAL_ERREUR);
        }

        if (utilisateur.getVille() == null || utilisateur.getVille().isBlank()){
            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_VILLE_ERREUR);
        }

        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().isBlank() || utilisateur.getMotDePasse().length()<8){
            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_MOTDEPASSE_ERREUR);
        }
    }

    // Methode afin de valider la connexion.
    // On utilise la methode selectMotDePasse du DAO Utilisateur.
    // Si il n'y a pas de mot de passe retourné, alors il y a un probleme de login (pseudo ou mail)
    // sinon si il y a un mot de passe retourné par le selectMotDePasse alors on le compare à celui saisi par l'utilisateur.
    public void validateConnexion(Utilisateur utilisateur, BusinessException businessException) throws SQLException{

        if(utilisateurDAO.selectMotDePasse(utilisateur) == null || utilisateurDAO.selectMotDePasse(utilisateur).isBlank()){

            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_TEST_MOTDEPASSE_CONNEXION_USER_LOGIN_ERREUR);

        } else if (utilisateur.getMotDePasse().equals(utilisateurDAO.selectMotDePasse(utilisateur))) {

            businessException.ajouterErreur(CodesResultatBLL.REGLE_USER_TEST_MOTDEPASSE_CONNEXION_USER_MOTDEPASSE_ERREUR);
        }
    }
}
