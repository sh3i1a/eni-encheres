<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="fr.eni.encheres.messages.LecteurMessage"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Connexion</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/connexion.css" />
</head>
<body>
<header>
    <div class="logo">
        <a href="${pageContext.request.contextPath}/accueil">COCOWIKI</a>
    </div>

    <c:if test="${connecte}">
        <div class="buttons">
            <a class="logout" href="" hidden>Mes Enchères</a>
            <a class="logout" href="${pageContext.request.contextPath}/NouvelleVente" hidden>Vendre un Article</a>
            <a class="logout" href="${pageContext.request.contextPath}/MonProfil">Mon Profil</a>
            <a class="account" href="${pageContext.request.contextPath}/deconnexion">Se Déconnecter</a>

        </div>
    </c:if>
    <c:if test="${!connecte}">
        <div class="buttons">
            <a class="logout" href="${pageContext.request.contextPath}/connexion">Se connecter</a>
            <a class="account" href="${pageContext.request.contextPath}/inscription">S'inscrire</a>
        </div>
    </c:if>
</header>
<main>
    <h1>Modifier votre mot de passe</h1>

    <c:if test="${!empty listeCodesErreur}">
        <div>
            <strong>Erreur!</strong>
            <ul>
                <c:forEach var="code" items="${listeCodesErreur}">
                    <li>${LecteurMessage.getMessageErreur(code)}</li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/ModifierMotDePasse" method="post">
        <label for="NouveauMotDePasse">Nouveau mot de passe</label>
        <input type="hidden" name="pseudo" value="${ sessionScope.utilisateur.pseudo }">
        <input type="hidden" name="noUtilisateur" value="${sessionScope.utilisateur.noUtilisateur}">
        <input type="password" name="mot_de_passe" id="NouveauMotDePasse" placeholder="Entrez votre mot de passe" minlength="8" maxlength="30" required />
        <label for="confirm_mot_de_passe">Confirmation du mot de passe</label>
        <input type="password" name="confirm_mot_de_passe" id="confirm_mot_de_passe" placeholder="Confirmez votre mot de passe" minlength="8" maxlength="30" required/>

        <button type="submit">Confirmer la modification</button>
    </form>
    <p>Retourner a mon profil<a href="${pageContext.request.contextPath}/MonProfil">Retour à l'accueil</a></p>
</main>
</body>
</html>