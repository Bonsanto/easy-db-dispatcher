/// <reference path="../modules/node.d.ts" />

var http = require("request"),
	pg = require("pg");

var maxPokemon:number = 718,
	endpoint:string = "http://pokeapi.co/api/v1/pokemon/",
	connection:string = "postgres://postgres:masterkey@localhost/pokemon",
	insertQuery:string = "INSERT INTO pokemon VALUES(?,'?')",
	client = new pg.Client(connection);


client.connect(error => {
	var currentPokemon:number = 1;
	var request = (pokemon:number) => {
		if (pokemon <= maxPokemon) {
			http(endpoint + pokemon, (err, response, body) => {
				if (err) console.log(err);
				else if (body) {
					var pokemonData = JSON.parse(body);
					console.log(pokemonData.name);
					client.query(insertQuery.replace(/\?/g, (char:string, position:number, word:string) =>
							word.indexOf(char) === position ? pokemonData.national_id : pokemonData.name
					), (err, res) => {
						if (!err) request(++pokemon);
						else console.log(err);
					});
				}
			});
		} else {
			console.log("Finished querying");
			client.end();
		}
	};

	if (error) console.log("Couldn't connect to postgres", error);

	request(currentPokemon);
});
