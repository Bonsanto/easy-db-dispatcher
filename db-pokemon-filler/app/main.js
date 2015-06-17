/// <reference path="../modules/node.d.ts" />
var http = require("request"), pg = require("pg");
var maxPokemon = 718, endpoint = "http://pokeapi.co/api/v1/pokemon/", connection = "postgres://postgres:masterkey@localhost/pokemon", insertQuery = "INSERT INTO pokemon VALUES(?,'?')", client = new pg.Client(connection);
client.connect(function (error) {
    var currentPokemon = 1;
    var request = function (pokemon) {
        if (pokemon <= maxPokemon) {
            http(endpoint + pokemon, function (err, response, body) {
                if (err)
                    console.log(err);
                else if (body) {
                    var pokemonData = JSON.parse(body);
                    console.log(pokemonData.name);
                    client.query(insertQuery.replace(/\?/g, function (char, position, word) { return word.indexOf(char) === position ? pokemonData.national_id : pokemonData.name; }), function (err, res) {
                        if (!err)
                            request(++pokemon);
                        else
                            console.log(err);
                    });
                }
            });
        }
        else {
            console.log("Finished querying");
            client.end();
        }
    };
    if (error)
        console.log("Couldn't connect to postgres", error);
    request(currentPokemon);
});
//# sourceMappingURL=main.js.map