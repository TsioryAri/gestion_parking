#!/bin/bash
# Supprime les données créées par les scripts de test (places de la zone TEST, utilisateurs j6-*)
docker exec -i parking-postgres psql -U parking_user -d parking_db << 'SQL'
DELETE FROM reservations
  WHERE place_id IN (SELECT id FROM places WHERE zone LIKE 'TEST%')
     OR utilisateur_id IN (SELECT id FROM utilisateurs WHERE email LIKE 'j6-%');
DELETE FROM vehicules WHERE utilisateur_id IN (SELECT id FROM utilisateurs WHERE email LIKE 'j6-%');
DELETE FROM utilisateurs WHERE email LIKE 'j6-%';
DELETE FROM places WHERE zone LIKE 'TEST%';
SQL
