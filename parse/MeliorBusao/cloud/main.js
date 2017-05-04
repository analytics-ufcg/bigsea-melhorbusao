function getDriverEvaluation(driver) {
    return driver;
}

function getBusLoadEvaluation(loaded) {
    return loaded? 0 : 1;
}

Parse.Cloud.define("getAllSummaries", function(request, status) {
	var ratingTable = "Rating";
	var results     = [];
	var chunk_size  = 1000;
	
	var processCallback = function(res) {
		results = results.concat(res);
		if (res.length === chunk_size) { 
			process(res[res.length-1].updatedAt);
		} else {
			var routes = {};
			
			for (var i = 0; i < results.length; ++i) {
				var rota = results[i].get("rota");
				if (!routes.hasOwnProperty(rota)) {
					routes[rota] = {
							count: 0,
							media: 0,
							totalMotorista: 0,
							totalLotacao: 0,
							totalCondition: 0
					};
				}

				var sumario = routes[rota];
				sumario.count++;

				if (results[i].get("motorista"))
					sumario.totalMotorista += 1;
				if (!results[i].get("lotacao"))
					sumario.totalLotacao += 1;
				if (results[i].get("condition"))
					sumario.totalCondition += 1;
			}
			status.success(routes); 
		}
	};

	var process = function(skip) {
		var query = new Parse.Query(ratingTable);
		if (skip) {
			query.greaterThan("objectId", skip);
		}
		query.limit(chunk_size);
		query.ascending("objectId");
		query.find().then(function (res) {
			processCallback(res);
		}, function (error) {
			status.error("query unsuccessful, length of result " + results.length + ", error:" + error.code + " " + error.message);
		});
	};
	process(false);
});


Parse.Cloud.define("sumarios", function(request, response) {
    var query = new Parse.Query("Rating");
    query.find({
        success: function(results) {
            var routes = {};

            for (var i = 0; i < results.length; ++i) {
                var rota = results[i].get("rota");

                if (!routes.hasOwnProperty(rota)) {
                    routes[rota] = {
                        count: 0,
                        media: 0,
                        totalMotorista: 0,
                        totalLotacao: 0
                    };
                }

                var sumario = routes[rota];

                sumario.count++;
                sumario.media += results[i].get("nota");

                if (results[i].get("motorista"))
                    sumario.totalMotorista += 1;
                if (results[i].get("lotacao"))
                    sumario.totalLotacao += 1;
            }

            response.success(routes);
        },
        error: function() {
            response.error("rota lookup failed");
        }
    });
});

Parse.Cloud.define("verifyBigSeaToken", function(request, response) {
	var bigSeatoken = request.params.token;
	var username = request.params.username;

	Parse.Cloud.httpRequest({
	  method: 'POST',
	  url: 'https://eubrabigsea.dei.uc.pt/engine/api/verify_token',
	  params: {
		token : bigSeatoken
	  }
	}).then(function(httpResponse) {
		if (httpResponse.data.response == username) {
			response.success(true);
			console.log(httpResponse.data.response);
		} else {
			response.error('invalid token');
			console.error('Token does not belong to user');
		}
		response.success(httpResponse.data.response == username);
	}, function(httpResponse) {
		console.error('Request failed with response code ' + httpResponse.status);
		response.error(httpResponse.status);
	});
});

Parse.Cloud.define("insertRating", function(request, response) {
    var bigSeatoken = request.params.token;
    var username = request.params.username;
    var rating = JSON.parse(request.params.rating);

    var Rating = Parse.Object.extend("Rating");

    var ratingTable = new Rating();

    ratingTable.save(rating, {
        success: function(ratingTable) {
            // The object was saved successfully.
            response.success("ok");
        },
        error: function(ratingTable, error) {
            // The save failed.
            // error is a Parse.Error with an error code and message.
            response.error(error);
        }
    });
});

