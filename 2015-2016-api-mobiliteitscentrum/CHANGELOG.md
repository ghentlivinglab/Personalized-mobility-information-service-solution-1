### CHANGELOG

#### 2.1.0 (19/03/2016)

##### Algemeen

* Er zijn een aantal sequentiediagrammen beschikbaar in `diagrams/`
* Aan alle endpoints waar authenticatie en/of authorizatie is vereist, is een `Authorization` header toegevoegd, die de access token bevat. Indien deze niet valid is, zal een `401 UNAUTHORIZED` of `403 FORBIDDEN` teruggegeven worden.

##### Endpoints

* `/refresh_token/regular`: Toegevoegd. Endpoint om refresh token te bemachtigen met email & wachtwoord combinatie
* `/refresh_token/facebook`: Toegevoegd. Endpoint om refresh token te bemachtigen met facebook login (eventuele registratie met facebook wordt hier ook gedaan)
* `/access_token/`: Toegevoegd. Endpoint om access token te bemachtigen met refresh token
* `/user/forgot_password`: Toegevoegd. Endpoint voor gebruikers die hun wachtwoord vergeten zijn. De `Authorization` header is de code die men terugkrijgt van reCaptcha.
* `/user/{user_id}/`: Gewijzigd. `POST` methode heeft nu een `Authorization` header, die de code die men terugkrijgt van reCaptcha moet bevatten.
* `/user/{user_id}/verify`: Toegevoegd. Endpoint voor het valideren van attributen van user (bvb. email, cell number)
* `/user/{user_id}/point_of_interest/`: Toegevoegd. Endpoint voor het opvragen en aanmaken van POIs over een user
* `/user/{user_id}/point_of_interest/{poi_id}/`: Toegevoegd. Endpoint voor het opvragen, aanpassen en verwijderen van een POI
* `/event/{event_id}/`: Gewijzigd. DELETE methode toegevoegd. Om events te verijderen. Alleen operators mogen dit doen.
* `/user/{user_id}/travel/{travel_id}/route/`: Toegevoegd. Endpoint voor alle routes van een travel op te vragen en routes bij te maken.
* `/user/{user_id}/travel/{travel_id}/route/{route_id}/`: Toegevoegd. Endpoint voor een route op te vragen, te wijzigen en te verwijderen uit een travel.
* `/admin/data_dump/`: Toegevoegd. Endpoint voor een data-dump te krijgen die de database voorsteld. 
* `/transportationtype/`: Toegevoegd. Endpoint voor alle transportation types op te vragen die de server begrijpt.

##### Models

* `User`: Alle velden zijn optioneel, behalve `User.email` (en `User.password` tijdens `POST /user/` en `PUT /user/{user_id}/`)
* `User.validated`: User heeft er een `validated` property bijgekregen, waarin staat welke attributen er al gevalideerd werden (bvb. email, cell number)
* `User.points_of_interest`: Het attribuut `points_of_interest` van User is verwijderd, sinds deze nu zijn eigen endpoint heeft
* `User.gender`: Verwijderd.
* `PointOfInterest.notify_for_event_types`: Toegevoegd. Hiermee kan een user selecteren voor welke eventtypes hij wilt verwittigd worden in deze POI.
* `Address.is_home_address`: Verwijderd.
* `Route.start_point`: Verwijderd. We zien een route vanaf nu als een manier om van A naar B te geraken. De A en B liggen vast in de travel.
* `Route.end_point`: Verwijderd. We zien een route vanaf nu als een manier om van A naar B te geraken. De A en B liggen vast in de travel.
* `Route.transportation_types`: Veranderd naar `Route.transportation_type`. Een route kan maar 1 transportation type hebben.
* `Route.notify`: Toegevoegd. Bevat de manieren waarop de gebruiker wilt genotified worden over deze route.
* `Route.active`: Toegevoegd. Indiceert of de gebruiker notificaties wilt ontvangen over deze route.
* `Event.jam`: Veranderd naar `Event.jams`. Dit is nu een array van jams, die eventueel leeg kan zijn (of 1 element bevatten, of meerdere).
* `Event.relevant_for_transportation_types`: Toegevoegd. Niet alle events van een bepaald event type zijn relevant voor alle transportatietypes.
* `EventType.subtype`: Verwijderd.
* `EventType.relevant_for_transportation_types`: Verplaatst naar `Event.relevant_for_transportation_types`.
* `Travel.date`: Verwijderd.
* `Travel.route`: Verwijderd. Routes zitten nu op hun eigen endpoint.
* `Travel.start_point`: Toegevoegd. Komt van `Route.start_point`. Alle routes op dezelfde travel hebben hetzelfde start- en eindpunt, maar niet dezelfde manier om daar te geraken.
* `Travel.end_point`: Toegevoegd. Komt van `Route.end_point`. Alle routes op dezelfde travel hebben hetzelfde start- en eindpunt, maar niet dezelfde manier om daar te geraken.

#### 1.1.5 (2/03/2016)

* Alles voor Milestone 1