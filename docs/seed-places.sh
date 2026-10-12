#!/bin/bash
BASE=http://localhost:8082
creer() {
  code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/parking/spaces" \
    -H "Content-Type: application/json" \
    -d "{\"numero\":\"$1\",\"zone\":\"$2\",\"type\":\"$3\",\"statut\":\"$4\"}")
  echo "$code  $1"
}

# Zone A : places standard et PMR
creer A-01 A STANDARD LIBRE
creer A-02 A STANDARD OCCUPEE
creer A-03 A STANDARD OCCUPEE
creer A-04 A STANDARD LIBRE
creer A-05 A STANDARD RESERVEE
creer A-06 A STANDARD LIBRE
creer A-07 A STANDARD HORS_SERVICE
creer A-08 A STANDARD LIBRE
creer A-09 A PMR LIBRE
creer A-10 A PMR OCCUPEE
# Zone B : places VIP
creer B-01 B VIP LIBRE
creer B-02 B VIP OCCUPEE
creer B-03 B VIP RESERVEE
creer B-04 B VIP LIBRE
creer B-05 B VIP OCCUPEE
creer B-06 B VIP LIBRE
# Zone C : places abonnés
creer C-01 C ABONNE OCCUPEE
creer C-02 C ABONNE LIBRE
creer C-03 C ABONNE RESERVEE
creer C-04 C ABONNE OCCUPEE
creer C-05 C ABONNE LIBRE
creer C-06 C ABONNE HORS_SERVICE
