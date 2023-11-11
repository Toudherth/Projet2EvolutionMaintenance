# Projet2EvolutionMaintenance
Projet sur la compréhention des programmes, clusturing et Spoon

# Le fonctionnement de la partie 1 
une fois on execute le porgramme on aura un champs de texte scanner pour ajouter le path de projet que vous voullez analyser, le path soit le chemin de projet sans le fichiers src. 
le programme vous renvoie un menu pour choisir ce que vous voulez 1, 2... 

# Choix 1 : Calculer la métrique de couplage entre deux classes A et B 
le programme vous renvoie la liste des classes qui existe dans le projet, il vous propose de choisir deux classes A et B, apres il vous renvoie la valeur de couplage (A,B) entre la classe A et la classe B qui est :  \n

Couplage(A,B) = nbr relation betwenn A and B / nbr total relations AB.

le programme vous renvoie un autre choix 5 qui vous permet de recuperer le graphe d'appel, et ce graphe d'appel sera renvoyer dans la console ainsi que une image png qu'on trouve dans le projet actuel.

# Choix 2 : Générer le regroupement hiérarchique des clusters.

Le choix 2 vos renvoie l'ensemble des clusters de projets.

# Choix 3 : Générer l'algorithme d'identification des modules.
Le choix 3  renvoie la listes des modules generer par l'algorithme d'identification, qui contient les clusters qui ont un couplage fort.

# Choix 4 : Calculer la métrique de couplage entre deux classes A et B en utilisant Spoon.


# Choix 5 : Générer le regroupement hiérarchique des classes (avec Spoon).

# Choix 6 : Générer l'algorithme d'identification des modules (avec Spoon).

# Choix 0 : Quitter 







