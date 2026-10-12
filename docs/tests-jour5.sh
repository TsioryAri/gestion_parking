#!/bin/bash
BASE=http://localhost:8082
TS=$(date +%s)
OK=0; KO=0

verdict() {
  if [ "$2" = "0" ]; then echo "OK     $1"; OK=$((OK+1));
  else echo "ECHEC  $1 : $3"; KO=$((KO+1)); fi
}

get_page() {   # nom, chemin, texte attendu
  code=$(curl -s -o /tmp/page.html -w "%{http_code}" "$BASE$2")
  if [ "$code" = "200" ] && grep -qF -- "$3" /tmp/page.html; then verdict "$1" 0
  else verdict "$1" 1 "code $code, texte '$3' introuvable"; fi
}

post_form() {  # nom, chemin, donnees, code attendu, texte attendu (optionnel)
  code=$(curl -s -o /tmp/page.html -w "%{http_code}" -X POST "$BASE$2" -d "$3")
  if [ "$code" != "$4" ]; then verdict "$1" 1 "attendu $4, obtenu $code"
  elif [ -n "$5" ] && ! grep -qF -- "$5" /tmp/page.html; then verdict "$1" 1 "texte '$5' introuvable"
  else verdict "$1" 0; fi
}

echo "--- Fichiers statiques et pages"
for f in /css/style.css /js/app.js /js/plan.js; do
  code=$(curl -s -o /dev/null -w "%{http_code}" "$BASE$f")
  if [ "$code" = "200" ]; then verdict "Fichier $f" 0; else verdict "Fichier $f" 1 "code $code"; fi
done
code=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/")
if [ "$code" = "302" ]; then verdict "Racine redirige vers le plan" 0; else verdict "Racine redirige vers le plan" 1 "code $code"; fi
get_page "Plan du parking" /plan "Plan du parking"
get_page "Liste des places" /places 'href="/places/nouvelle"'
get_page "Places disponibles" /places/disponibles "disponible"
get_page "Formulaire place" /places/nouvelle 'action="/places"'
get_page "Liste des utilisateurs" /utilisateurs 'href="/utilisateurs/nouveau"'
get_page "Formulaire utilisateur" /utilisateurs/nouveau 'action="/utilisateurs"'
get_page "Liste des vehicules" /vehicules 'href="/vehicules/nouveau"'
get_page "Formulaire vehicule" /vehicules/nouveau 'action="/vehicules"'

echo "--- Formulaire place"
post_form "Place invalide (champs vides)" /places "numero=&zone=&type=" 200 "obligatoire"
post_form "Place valide" /places "numero=W-$TS&zone=TEST&type=STANDARD&statut=LIBRE" 302
post_form "Place numero deja utilise" /places "numero=W-$TS&zone=TEST&type=VIP&statut=LIBRE" 200 "existe"
get_page "Nouvelle place visible dans le plan" /plan "W-$TS"
get_page "Nouvelle place visible dans les places disponibles" /places/disponibles "W-$TS"

echo "--- Formulaire utilisateur"
post_form "Utilisateur invalide" /utilisateurs "nom=&email=pas-un-email" 200 "obligatoire"
post_form "Utilisateur valide" /utilisateurs "nom=Test+$TS&email=w$TS@example.com&typeUtilisateur=VIP" 302
post_form "Utilisateur email deja utilise" /utilisateurs "nom=Autre&email=w$TS@example.com" 200 "existe"

echo "--- Formulaire vehicule"
USER_ID=$(curl -s "$BASE/api/users" | python3 -c "import sys,json; print(next(u['id'] for u in json.load(sys.stdin) if u['email']=='w$TS@example.com'))")
post_form "Vehicule invalide" /vehicules "immatriculation=&utilisateurId=" 200 "obligatoire"
post_form "Vehicule valide" /vehicules "immatriculation=W$TS&marque=Toyota&modele=Yaris&typeVehicule=VOITURE&utilisateurId=$USER_ID" 302
post_form "Vehicule immatriculation deja utilisee (casse ignoree)" /vehicules "immatriculation=w$TS&utilisateurId=$USER_ID" 200 "existe"
post_form "Vehicule proprietaire inexistant" /vehicules "immatriculation=Z$TS&utilisateurId=999999" 200 "introuvable"
get_page "Vehicule dans la liste" /vehicules "W$TS"

# Nettoyage du vehicule et de l'utilisateur de test (les places de test sont dans la zone TEST)
VEH_ID=$(curl -s "$BASE/api/vehicles" | python3 -c "import sys,json; print(next(v['id'] for v in json.load(sys.stdin) if v['immatriculation']=='W$TS'))")
curl -s -o /dev/null -X DELETE "$BASE/api/vehicles/$VEH_ID"
curl -s -o /dev/null -X DELETE "$BASE/api/users/$USER_ID"

echo
echo "Resultat : $OK OK, $KO echec(s)"
