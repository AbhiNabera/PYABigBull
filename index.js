const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();


//function to addPlayer
exports.addPlayer = functions.https.onRequest((req, res) => {

	if(req.method !== "POST"){
		return res.status(400).send('Please send a POST request');
	}

    const phoneNumber = req.body.phoneNumber;
    const userName = req.body.userName;
    const prevUserName = req.body.prevUserName;

    var data = {

    	"phoneNumber" : phoneNumber,
    	"userName" : userName,
    	"active": true
    };
            

    return admin.database().ref('/Players').child('/' + phoneNumber).once('value', function(snapshot) {
       

       if(!snapshot.exists()) {

       		//when phoneNumber does not exist. initialize user

	       	return admin.database().ref('/Players').child('/'+phoneNumber).child('/active').once('value', function(snapshot) {

	       		if(snapshot.val() === true || snapshot.val() === null || snapshot.val() === undefined ){ 

		       		return admin.database().ref('/UserNames').child('/' + userName).once('value', function(snapshot) {

		       			if(!snapshot.exists()) {

		       				return admin.database().ref('/Players').child('/' + phoneNumber).update(data).then((snapshot)=>{

		       					return admin.database().ref('/ADMINSETTINGS').once('value', function(snapshot) {

		       						return admin.database().ref('/Players').child('/' + phoneNumber).child('/Account').update(initialUserSetUp(snapshot)).then((snapshot)=>{

		       							if(prevUserName !== "") {

					       					return admin.database().ref('/UserNames').child('/'+ prevUserName).remove().then((snapshot) =>{

					       						return admin.database().ref('/UserNames').child('/' +userName).set(data).then((snapshot)=>{

							  							return res.status(200).json({
							          						flag: "USER_ADDED",
							            					status: 200
							           	    			});
							  						});

					       					});

				       					}else {

				       						return admin.database().ref('/UserNames').child('/' +userName).set(data).then((snapshot)=>{

							  							return res.status(200).json({
							          						flag: "USER_ADDED",
							            					status: 200
							           	    			});
							  						});
				       					}

		       						});
		       					});

			       			});

		       			}else {

		       				//username already exist
		       				if(snapshot.val().phoneNumber === phoneNumber) {

		       					return admin.database().ref('/Players').child('/' + phoneNumber).update(data).then((snapshot)=>{

		       						return admin.database().ref('/UserNames').child('/'+ prevUserName).remove().then((snapshot) =>{

		       							return admin.database().ref('/UserNames').child('/' +userName).set(data).then((snapshot)=>{

				  							return res.status(200).json({
				          						flag: "USER_ADDED",
				            					status: 200
				           	    			});
				  						});

		       						});
			       				});

		       				}else {
			       				return res.status(200).json({
			       					flag: "USERNAME_EXIST",
			       					status: 200
			       				});
			       			}
		       			}
		        		
		        	});

	       		}else {

	       			return res.status(200).json({
				          						flag: "USER_DISABLED",
				            					status: 200
				           	    			});
	       		}

	       	});	

       }else {
       	//phoneNumber exist

       		return admin.database().ref('/Players').child('/'+phoneNumber).child('/active').once('value', function(snapshot) {

	       		if(snapshot.val() === true || snapshot.val() === null || snapshot.val() === undefined ){ 

		       		return admin.database().ref('/UserNames').child('/' + userName).once('value', function(snapshot) {

		       			if(!snapshot.exists()) {

		       				return admin.database().ref('/Players').child('/' + phoneNumber).update(data).then((snapshot)=>{

		       					if(prevUserName !== "") {

			       					return admin.database().ref('/UserNames').child('/'+ prevUserName).remove().then((snapshot) =>{

			       						return admin.database().ref('/UserNames').child('/' +userName).set(data).then((snapshot)=>{

					  							return res.status(200).json({
					          						flag: "USER_ADDED",
					            					status: 200
					           	    			});
					  						});

			       					});

		       					}else {

		       						return admin.database().ref('/UserNames').child('/' +userName).set(data).then((snapshot)=>{

					  							return res.status(200).json({
					          						flag: "USER_ADDED",
					            					status: 200
					           	    			});
					  						});
		       					}

			       			});

		       			}else {

		       				//username already exist
		       				if(snapshot.val().phoneNumber === phoneNumber) {

		       					return admin.database().ref('/Players').child('/' + phoneNumber).update(data).then((snapshot)=>{

		       						return admin.database().ref('/UserNames').child('/'+ prevUserName).remove().then((snapshot) =>{

		       							return admin.database().ref('/UserNames').child('/' +userName).set(data).then((snapshot)=>{

				  							return res.status(200).json({
				          						flag: "USER_ADDED",
				            					status: 200
				           	    			});
				  						});

		       						});
			       				});

		       				}else {
			       				return res.status(200).json({
			       					flag: "USERNAME_EXIST",
			       					status: 200
			       				});
			       			}
		       			}
		        		
		        	});

	       		}else {

	       			return res.status(200).json({
				          						flag: "USER_DISABLED",
				            					status: 200
				           	    			});
	       		}

	       	});	
       }  

   	});
        
 });


//function to get username
exports.userName = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

    var phoneNumber = req.query.phoneNumber;
    
    return admin.database().ref('/Players').child('/' + phoneNumber).child('/userName').once('value', function(snapshot) {
       	
       	if(snapshot.exists()) {

       		return res.json({
       			"userName" : snapshot.val(),
       			"status" : 200 
       		});   

       	}else {

       		return res.json({
       			"status" : 200 
       		});   
       	}
       	

   	}).catch(exception => {
		    		console.log('Error!: ' + exception)

		  		});

});    


exports.expiry = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	return admin.database().ref('/EXPIRY').once('value', function(snapshot) {

		return res.json({
       			"expiry" : snapshot.val(),
       			"status" : 200 
       		});  
	});

});


exports.userStatus = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	var phoneNumber = req.query.phoneNumber;

	return admin.database().ref('/Players').child('/'+ phoneNumber).child('/active').once('value', function(snapshot) {

		var parentsnapshot = snapshot;

		return admin.database().ref('/Players').child('/'+ phoneNumber).child('/Account').once('value', function(snapshot) {

			if(snapshot.exists()) {
				
				return res.json({
       				"isActive" : parentsnapshot.val(),
       				"status" : 200 
       			});

			}else {

				return admin.database().ref('/ADMINSETTINGS').once('value', function(snapshot) {

		       		return admin.database().ref('/Players').child('/' + phoneNumber).child('/Account').update(initialUserSetUp(snapshot)).then((snapshot)=>{

		       			return res.json({
       						"isActive" : parentsnapshot.val(),
       						"status" : 200 
       					});

		       		});

		       	});	

			}
		});
		
	});

});


exports.userAccount = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	var phoneNumber = req.query.phoneNumber;

	return admin.database().ref('/Players').child('/'+ phoneNumber).child('/Account').once('value', function(snapshot) {

		if(snapshot.exists()) {
			return res.json({
	       			"data" : snapshot.val(),
	       			"status" : 200 
	       		});  
		}else {
			return res.json({
	       			"message" : "no data",
	       			"status" : 200 
	       	});  
		}
	});

});

exports.adminSettings = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	return admin.database().ref('/ADMINSETTINGS').once('value', function(snapshot) {
		return res.json({
	   		"data" : snapshot.val(),
	       	"status" : 200 
	    });  
		
	});

});


exports.transaction = functions.https.onRequest((req, res) => {

	if(req.method !== "POST"){
		return res.status(400).send('Please send a POST request');
	}

	var phoneNumber = req.body.phoneNumber;
	var Account = req.body.Account;

    var db_ref = admin.database().ref('/Players').child('/'+phoneNumber).child('/Account');

    return db_ref.update(Account).then((snapshot)=>{
    	return res.json({
	   		"message" : "transaction successful",
	       	"status" : 200 
	    }); 
    });
});


exports.buytransaction = functions.https.onRequest((req, res) => {

	if(req.method !== "POST"){
		return res.status(400).send('Please send a POST request');
	}

	var phoneNumber = req.body.phoneNumber;
	var txn_id = req.body.txn_id;
	var type = req.body.product_type;
	var Account = req.body.Account;
	var bought_item = req.body.bought_item;
	var transaction = req.body.transaction;
	var txn_history = req.body.txn_history;
	var txn_summary = req.body.txn_summary;

    var db_ref = admin.database().ref('/Players').child('/'+phoneNumber).child('/Account');

    return db_ref.update(Account).then((snapshot)=>{

    	return db_ref.child('/stocks_list').child('bought_items').child('/'+type).update(bought_item).then((snapshot)=>{

    		return db_ref.child('/stocks_list').child('bought_items').child('/'+type).child('/'+txn_id).update(transaction).then((snapshot)=>{
    		
    			return db_ref.child('/txn_history').child('/'+txn_id).set(txn_history).then((snapshot)=>{

    				return db_ref.child('/txn_history').child('/'+txn_id).child('/txn_summary').set(txn_summary).then((snapshot)=>{

    					return res.json({
	   						"message" : "transaction successful",
	       					"status" : 200 
	    				}); 

    				}); 
    			});
    		});
    	});
    });
});


exports.selltransaction = functions.https.onRequest((req, res) => {

	if(req.method !== "POST"){
		return res.status(400).send('Please send a POST request');
	}

	var phoneNumber = req.body.phoneNumber;
	var txn_id = req.body.txn_id;
	var type = req.body.product_type;
	var Account = req.body.Account;
	var sold_item = req.body.sold_item;
	var bought_item = req.body.bought_item;
	var transaction = req.body.transaction;
	var txn_history = req.body.txn_history;
	var txn_summary = req.body.txn_summary;

	var bought_data = {
		"qty": bought_item.left_qty,
		"total_amount": bought_item.left_investment
	};

	console.log("data: "+ bought_data);

    var db_ref = admin.database().ref('/Players').child('/'+phoneNumber).child('/Account');

    return db_ref.update(Account).then((snapshot)=>{

    	return db_ref.child('/stocks_list').child('sold_items').child('/'+type).update(sold_item).then((snapshot)=>{

    		return db_ref.child('/stocks_list').child('sold_items').child('/'+type).child('/'+txn_id).update(transaction).then((snapshot)=>{
    		
    			return db_ref.child('/txn_history').child('/'+txn_id).set(txn_history).then((snapshot)=>{

    				return db_ref.child('/txn_history').child('/'+txn_id).child('/txn_summary').set(txn_summary).then((snapshot)=>{

    					if(parseInt(bought_item.left_qty) > 0) {

    						return db_ref.child('/stocks_list').child('/bought_items').child('/'+type).child('/'+txn_id).child('/qty').update(bought_item.left_qty).then((snapshot) =>{

    							return db_ref.child('/stocks_list').child('/bought_items').child('/'+type).child('/'+txn_id).child('/total_amount').update(bought_item.total_amount).then((snapshot)=>{

    								return res.json({
	   									"message" : "transaction successful",
	       								"status" : 200 
	    							}); 

    							});
    						});

    					}else {

    						return db_ref.child('/stocks_list').child('/bought_items').child('/'+type).child('/'+txn_id).remove().then((snapshot) =>{

    							return res.json({
	   								"message" : "transaction successful",
	       							"status" : 200 
	    						}); 

    						});
    					}

    				}); 
    			});
    		});
    	});
    });
});


exports.playerinfo = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	var phoneNumber = req.query.phoneNumber;

	return admin.database().ref('/Players').child("/"+phoneNumber).once('value', function(snapshot) {

		var Account = {
			"avail_balance": snapshot.child('Account').val().avail_balance,
			"change": snapshot.child('Account').val().change,
			"investment": snapshot.child('Account').val().investment,
			"percentchange": snapshot.child('Account').val().percentchange,
			"start_balance": snapshot.child('Account').val().start_balance,
			"stocks_count": snapshot.child('Account').val().stocks_count,
			"txn_history": toArray(snapshot.child('Account').child('txn_history'))
		};

		var data = {
			"phoneNumber": snapshot.val().phoneNumber,
			"userName": snapshot.val().userName,
			"active": snapshot.val().active,
			"Account": Account
		};

		return res.json({
	   		"data" : data,
	       	"status" : 200 
	    });  
		
	});

});


exports.txnHistory = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	var phoneNumber = req.query.phoneNumber;

	return admin.database().ref('/Players').child("/"+phoneNumber).child('/Account').child('/txn_history').once('value', function(snapshot) {

		return res.json({
	   		"data" : toArray(snapshot),
	       	"status" : 200 
	    });  
		
	});

});


exports.buyList = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	var phoneNumber = req.query.phoneNumber;

	return admin.database().ref('/Players').child("/"+phoneNumber).child('/Account').child('/stocks_list').child('/bought_items').once('value', function(snapshot) {

		var data = {

			"index" : stockstoArray(snapshot.child('/index')),
			"commodity" : stockstoArray(snapshot.child('/commodity')),
			"currency" : stockstoArray(snapshot.child('/currency'))
		};

		return res.json({
	   		"data" : data,
	       	"status" : 200 
	    });  
		
	});

});


exports.sellList = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	var phoneNumber = req.query.phoneNumber;

	return admin.database().ref('/Players').child("/"+phoneNumber).child('/Account').child('/stocks_list').child('/sold_items').once('value', function(snapshot) {

		var data = {

			"index" : stockstoArray(snapshot.child('/index')),
			"commodity" : stockstoArray(snapshot.child('/commodity')),
			"currency" : stockstoArray(snapshot.child('/currency'))
		};

		return res.json({
	   		"data" : data,
	       	"status" : 200 
	    });  
		
	});

});


exports.leaderboard = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	return admin.database().ref('Players').orderByChild('percentchange').once('value', function(snapshot) {
		return res.json({
	   		"data" : formatLeaderboard(snapshot),
	       	"status" : 200 
	    });  
	});

});


//supporting functions

function formatLeaderboard(snapshot) {

	var returnArr = [];
    //console.log(snapshot);

    if(!snapshot.exists()) return returnArr;

    snapshot.forEach(function(childSnapshot) {
       
       		var data = {
       			"phoneNumber" : childSnapshot.phoneNumber,
       			"userName" : childSnapshot.userName,
       			"percentchange" : childSnapshot.child('/Account').percentchange,
       			"change" : childSnapshot.Account.child('/Account').change
       		};

        	returnArr.push(data);
      
    });

    return returnArr;

}

function initialUserSetUp(snapshot) {
    
    var data = {

    	"avail_balance" : snapshot.val().initial_user_amt,
      	"investment" : "00000",
      	"stocks_count" : "0",
      	"start_balance" : snapshot.val().initial_user_amt,
      	"percentchange" : "0",
      	"change" : "0",
      	"shares_price" : "0"
    }

    return data;
}

function toArray(snapshot) {
    var returnArr = [];
    console.log(snapshot);

    if(!snapshot.exists()) return returnArr;

    snapshot.forEach(function(childSnapshot) {
       
        	returnArr.push(childSnapshot.val());
      
    });

    return returnArr;
}

function stockstoArray(snapshot) {
    var returnArr = [];
    console.log(snapshot);

    if(!snapshot.exists()) return returnArr;

    snapshot.forEach(function(childSnapshot) {
       
       	if(childSnapshot.key.indexOf("txn") !== -1) {
        	returnArr.push(childSnapshot.val());
       	}
      
    });

    return returnArr;
}

