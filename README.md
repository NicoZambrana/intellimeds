# intellimeds
Aplicación para Android en Java de un pastillero inteligente

----------------------------------------------------------------
De momento para poder organizarnos, comentaré los atributos que deben tener los medicamentos en la base de datos usando para ello SQLLite,
estos son los siguientes:
-ID(int): Para diferenciar los medicamentos
-Nombre(String)
-Dosis(int)
-Horario(posiblemente string): Horas en las que se toma
-Días(string): Para saber que días se lo debe tomar.
-Tomado(0 o 1): Para saber si fue tomada o no //Añadir más tarde como extra quizás ---> 1-Ha sido tomado 0-Aun no
