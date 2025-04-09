CREATE TABLE IF NOT EXISTS avis_escale (
                                           numeroAvis BIGINT PRIMARY KEY,
                                           numeroEscale VARCHAR(50),
    nomPort VARCHAR(100),
    codePort VARCHAR(10),
    nomNavire VARCHAR(100),
    nomOperateur VARCHAR(100),
    etat VARCHAR(50)
    );

INSERT INTO avis_escale (
    numeroAvis, numeroEscale, nomPort, codePort, nomNavire, nomOperateur, etat
) VALUES (
             1122334455, 'ESC98765', 'Port Tanger Med', 'MATNG', 'Navire Neptune', 'Operateur Maritime ABC', 'En attente'
         );