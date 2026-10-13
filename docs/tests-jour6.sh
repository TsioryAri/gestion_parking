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
t() { date -d "$1" +%Y-%m-%dT%H:%M:%S; }
resa() { echo "{\"utilisateurId\":$1,\"vehiculeId\":$2,\"placeId\":$3,\"arriveePrevue\":\"$4\",\"departPrevu\":\"$5\"}"; }

T08=$(t "tomorrow 08:00"); T09=$(t "tomorrow 09:00"); T10=$(t "tomorrow 10:00"); T1030=$(t "tomorrow 10:30")
T11=$(t "tomorrow 11:00"); T1130=$(t "tomorrow 11:30"); T12=$(t "tomorrow 12:00"); T13=$(t "tomorrow 13:00")
T14=$(t "tomorrow 14:00"); TY10=$(t "yesterday 10:00"); TY12=$(t "yesterday 12:00")

echo "--- Preparation (utilisateurs, vehicules, places de test)"
code POST /api/users "{\"nom\":\"Jour6 A\",\"email\":\"j6-a-$TS@example.com\"}" >/dev/null; U1=$(jid)
code POST /api/users "{\"nom\":\"Jour6 B\",\"email\":\"j6-b-$TS@example.com\"}" >/dev/null; U2=$(jid)
code POST /api/vehicles "{\"immatriculation\":\"J6A-$TS\",\"utilisateurId\":$U1}" >/dev/null; V1=$(jid)
code POST /api/vehicles "{\"immatriculation\":\"J6B-$TS\",\"utilisateurId\":$U2}" >/dev/null; V2=$(jid)
code POST /api/parking/spaces "{\"numero\":\"J6-1-$TS\",\"zone\":\"TEST\",\"type\":\"STANDARD\"}" >/dev/null; P1=$(jid)
code POST /api/parking/spaces "{\"numero\":\"J6-2-$TS\",\"zone\":\"TEST\",\"type\":\"STANDARD\"}" >/dev/null; P2=$(jid)
code POST /api/parking/spaces "{\"numero\":\"J6-3-$TS\",\"zone\":\"TEST\",\"type\":\"STANDARD\",\"statut\":\"HORS_SERVICE\"}" >/dev/null; P3=$(jid)

echo "--- Creation et statut de la place"
c=$(code POST /api/reservations "$(resa $U1 $V1 $P1 $T10 $T12)"); check "Reservation valide (10h-12h)" 201 "$c"; R1=$(jid)
c=$(code GET /api/parking/spaces/$P1); check "Lecture de la place reservee" 200 "$c"
if grep -q '"statut":"RESERVEE"' /tmp/resp.json; then check "La place passe a RESERVEE" ok ok; else check "La place passe a RESERVEE" ok "statut different"; fi

echo "--- Chevauchements (409)"
c=$(code POST /api/reservations "$(resa $U2 $V2 $P1 $T11 $T13)"); check "Chevauchement partiel (11h-13h)" 409 "$c"
c=$(code POST /api/reservations "$(resa $U2 $V2 $P1 $T1030 $T1130)"); check "Chevauchement inclus (10h30-11h30)" 409 "$c"
c=$(code POST /api/reservations "$(resa $U2 $V2 $P1 $T09 $T13)"); check "Chevauchement englobant (9h-13h)" 409 "$c"

echo "--- Creneaux acceptes (201)"
c=$(code POST /api/reservations "$(resa $U2 $V2 $P1 $T12 $T14)"); check "Creneau adjacent apres (12h-14h)" 201 "$c"
c=$(code POST /api/reservations "$(resa $U2 $V2 $P1 $T08 $T10)"); check "Creneau adjacent avant (8h-10h)" 201 "$c"
c=$(code POST /api/reservations "$(resa $U2 $V2 $P2 $T10 $T12)"); check "Meme creneau sur une autre place" 201 "$c"

echo "--- Validation (400)"
c=$(code POST /api/reservations "$(resa $U1 $V1 $P2 $T14 $T13)"); check "Depart avant arrivee" 400 "$c"
c=$(code POST /api/reservations "$(resa $U1 $V1 $P2 $TY10 $TY12)"); check "Arrivee dans le passe" 400 "$c"
c=$(code POST /api/reservations "$(resa $U1 $V2 $P2 $T13 $T14)"); check "Vehicule d'un autre utilisateur" 400 "$c"
c=$(code POST /api/reservations '{}'); check "Corps vide" 400 "$c"
c=$(code POST /api/reservations '{"utilisateurId":1,"vehiculeId":1,"placeId":1,"arriveePrevue":"demain","departPrevu":"apres"}'); check "Dates au mauvais format" 400 "$c"

echo "--- Etats et ressources (409, 404)"
c=$(code POST /api/reservations "$(resa $U1 $V1 $P3 $T13 $T14)"); check "Place hors service" 409 "$c"
c=$(code POST /api/reservations "$(resa $U1 $V1 999999 $T13 $T14)"); check "Place inexistante" 404 "$c"
c=$(code POST /api/reservations "$(resa 999999 $V1 $P2 $T13 $T14)"); check "Utilisateur inexistant" 404 "$c"
c=$(code POST /api/reservations "$(resa $U1 999999 $P2 $T13 $T14)"); check "Vehicule inexistant" 404 "$c"

echo "--- Lecture"
c=$(code GET /api/reservations); check "GET liste des reservations" 200 "$c"
c=$(code GET /api/reservations/$R1); check "GET reservation par id" 200 "$c"
c=$(code GET /api/reservations/999999); check "GET reservation inexistante" 404 "$c"

echo
echo "Resultat : $OK OK, $KO echec(s)"
echo "Donnees de test creees (zone TEST, utilisateurs j6-*) : bash docs/nettoyer-tests.sh pour les supprimer"
