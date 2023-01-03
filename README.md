<link rel="stylesheet" href="readme.css">

# Projet de POO

Réalisation d'un jeu vidéo 2D : **UBomb**.

## Principes du jeu

Une princesse est détenue prisonnière par de méchants monstres verts. Votre mission, si vous l'acceptez, est d'aller la délivrer. Pour cela, vous devrez traverser plusieurs mondes, plus effrayants les uns que les autres. Des portes vous permettront de passer de monde en monde. Certaines portes seront fermées à clé et nécessiteront d'avoir une clé dans votre inventaire. Vous êtes un expert en explosif et utiliserez vos bombes pour détruire les obstacles devant vous et tuer les monstres qui vous attaqueront.

## Représentation du jeu

Chaque monde est représenté par une carte (rectangulaire ou carré) composée de cellules. Chaque cellule peut contenir :

-   le joueur ;
-   la princesse ;
-   des monstres ;
-   des éléments de décor (arbres, pierres...) infranchissables et
    indestructibles ;
-   des caisses destructibles et déplaçables ; 
-   des portes, ouvertes ou fermées, permettant d’évoluer entre les
    mondes ;
-   des clés pour débloquer les portes fermées ;
-   des bonus ou des malus qu'il est possible de ramasser.

Cahier des charges
=================

## Base du jeu
Les touches:
- Les touches, `[ENTER]`, `[ESCAPE]`, ainsi que les flèches directionnelles fonctionnent.

Monstres :
- Déplacement aléatoire tous les `1e10 / monsterVelocity`, déplacement en direction du joueur pendant le niveau de la princesse (`Algorithme AStar`). Ils infligent 1 dégat dès qu'ils touchent un joueur. Si le joueur reste avec le monstre sur la case il prendra des dégats tous les `playerInvincibility`. Les monstres ont une vie en plus tous les deux niveaux, et se déplacent de plus en plus vite au fur et à mesure qu'on se rapproche de la princesse. A l'inverse la vitesse diminue en nous éloignant. Lorsque que l'on parle de rapprochement et d'éloignement, on parle en terme de niveau et non de cases nous séparant de la princesse. Les monstres peuvent marcher sur les bonus ainsi que les bombes sans les ramasser mais ne peuvent pas marche sur les portes (ouvertes ou non) et ils ne peuvent pas déplacer des caisses.

Joueur:
- Ce dernier à un nombre de vie défini par le fichier `.properties`, si ce n'est pas le cas il en possède 5 par défaut. Il devient invincible dès qu'un monstre le touche (Comme pour ses vies, le temps d'invincibilité est variable et possède une valeur par défaut). Il possède un inventaire, clé, bombe, peuvent y être présents et être utilisés. Il peut marcher les portes ouvertes, marcher et ramasser les bonus, et marcher sur les monstres et la princesse. Au contact de cette dernière la partie se termine sur une victoire.

Princesse:
- Immobile, elle peut uniquement être en contact (se faire marcher dessus) avec le joueur.

Décors:
- Les décors sont infranchissables et immobiles sauf pour les caisses et les portes ouvertes. Ils peuvent être des arbres, des pierres, des portes, des caisses, une princesse.

Bonus:
- Les bonus sont fixes et récupérables par le joueur. Les monstres et le joueur peuvent marcher dessus. Ils peuvent être des coeurs, des clés, ou des bombes (et ses atouts).

Mondes:
- Générés par le `.properties`, ils peuvent être compresser ou non, le nombre peut changer, et les dimensions ne sont pas fixes (y compris entre les niveaux d'une même partie).

Pour aller plus loin:
- Nous avons réussi à faire la vie des monstres, ainsi que leurs déplacements sur le niveau de la princesse. Le dernier changeant la vitesse en fonction de la "distance" de la princesse est aussi fonctionnel (Si nous avons 5 niveaux et que la princesse est niveau 3, nous aurons, Niveau 1 vitesse de base, Niveau 2 = vitesse1 + 1, Niveau 3 = vitesse2 + 1, Niveau 4 = vitesse3 - 1 = vitesse2, Niveau 5 = vitesse4 - 1 = vitesse1).