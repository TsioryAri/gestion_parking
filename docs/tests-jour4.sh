#!/bin/bash
BASE=http://localhost:8082
TS=$(date +%s)
OK=0; KO=0

check() {
  if [ "$2" = "$3" ]; then echo "OK     $1 ($3)"; OK=$((OK+1));
  else echo "ECHEC  $1 : attendu $2, obtenu $3"; KO=$((KO+1)); fi
}

code() {
  if [ -n "$3" ]; then
    curl -s -o /tmp/resp.json -w "%{http_code}" -X "$1" "$BASE$2" -H "Content-Type: application/json" -d "$3"
  else
    curl -s -o /tmp/resp.json -w "%{http_code}" -X "$1" "$BASE$2"
  fi
}

jid() { python3 -c "import json;print(json.load(open('/tmp/resp.json'))['id'])"; }

echo "--- Places"
c=$(code POST /api/parking/spaces "{\"numero\":\"S-$TS\",\"zone\":\"A\",\"type\":\"VIP\"}")
check "POST place valide" 201 "$c"
PLACE_ID=$(jid)
c=$(code POST /api/parking/spaces '{}')
check "POST place vide" 400 "$c"
c=$(code POST /api/parking/spaces "{\"numero\":\"S-$TS\",\"zone\":\"A\",\"type\":\"PMR\"}")
check "POST place numero deja utilise" 409 "$c"
c=$(code POST /api/parking/spaces "{\"numero\":\"X-$TS\",\"zone\":\"A\",\"type\":\"BLABLA\"}")
check "POST place type invalide" 400 "$c"
c=$(code GET /api/parking/spaces/available)
check "GET places disponibles" 200 "$c"
c=$(code GET /api/parking/spaces/$PLACE_ID)
check "GET place par id" 200 "$c"
c=$(code GET /api/parking/spaces/999999)
check "GET place inexistante" 404 "$c"

echo "--- Utilisateurs"
c=$(code POST /api/users "{\"nom\":\"Test $TS\",\"email\":\"u$TS@example.com\",\"typeUtilisateur\":\"VIP\"}")
check "POST utilisateur valide" 201 "$c"
USER_ID=$(jid)
c=$(code POST /api/users '{"nom":"","email":"pas-un-email"}')
check "POST utilisateur invalide" 400 "$c"
c=$(code POST /api/users "{\"nom\":\"Autre\",\"email\":\"u$TS@example.com\"}")
check "POST utilisateur email deja utilise" 409 "$c"
c=$(code PUT /api/users/$USER_ID "{\"nom\":\"Modifie $TS\",\"email\":\"u$TS@example.com\",\"typeUtilisateur\":\"PMR\"}")
check "PUT utilisateur" 200 "$c"
c=$(code GET /api/users/$USER_ID)
check "GET utilisateur" 200 "$c"
c=$(code GET /api/users/999999)
check "GET utilisateur inexistant" 404 "$c"

echo "--- Vehicules"
c=$(code POST /api/vehicles "{\"immatriculation\":\"V-$TS\",\"marque\":\"Toyota\",\"modele\":\"Yaris\",\"utilisateurId\":$USER_ID}")
check "POST vehicule valide" 201 "$c"
VEH_ID=$(jid)
c=$(code POST /api/vehicles "{\"immatriculation\":\"W-$TS\",\"utilisateurId\":999999}")
check "POST vehicule proprietaire inexistant" 404 "$c"
c=$(code POST /api/vehicles "{\"immatriculation\":\"V-$TS\",\"utilisateurId\":$USER_ID}")
check "POST vehicule immatriculation deja utilisee" 409 "$c"
c=$(code POST /api/vehicles '{"marque":"Toyota"}')
check "POST vehicule sans immatriculation" 400 "$c"
c=$(code DELETE /api/users/$USER_ID)
check "DELETE utilisateur qui a un vehicule" 409 "$c"
c=$(code DELETE /api/vehicles/$VEH_ID)
check "DELETE vehicule" 204 "$c"
c=$(code DELETE /api/users/$USER_ID)
check "DELETE utilisateur" 204 "$c"
c=$(code GET /api/users/$USER_ID)
check "GET utilisateur supprime" 404 "$c"

echo
echo "Resultat : $OK OK, $KO echec(s)"
