const functions = require('firebase-functions');
const admin = require('firebase-admin');

/*
const os = require("os");
const path = require("path");
const cors = require("cors")({ origin: true });
const Busboy = require("busboy");
const fs = require("fs");
const mime = require('mime');
const UUID = require('uuid/v4');
*/

admin.initializeApp();

/*
const keyFilename="bigbull-c7557-firebase-adminsdk-l8xfj-408d236c79.json"; 
const projectId = "bigbull-c7557" 
const bucketName = `${projectId}.appspot.com`;

var config = {
  projectId: 'bigbull-c7557',
  keyFilename: 'bigbull-c7557-firebase-adminsdk-l8xfj-408d236c79.json'
};

const {Storage} = require('@google-cloud/storage');

const storage = new Storage({
  projectId: projectId,
  keyFilename: keyFilename
});

//const bucket = storage.bucket()
*/

//-------------/admin functions
exports.allPlayers = 
	functions.https.onRequest((req, res) => {

		if(req.method !== "GET"){
			return res.status(400).send('Please send a GET request');
		}

		var pageno = req.query.pageno;
		var startAt = ( (parseInt(pageno)<=0?0:(parseInt(pageno)-1))*20 );
		var endAt = parseInt(pageno)*20 - 1;

		return admin.database().ref('/Players')./*orderByKey().startAt(startAt).endAt(endAt).*/once('value')
				.then(snapshot=>{

					return res.status(200).json({
						        data: adminSnapshotToArray(snapshot),
		    					status: 200
						   });

				}).catch(exception=>{
	    			//console.log("player exception: " + exception);
	    			return res.status(200).json({
						        flag: "INTERNAL SERVER ERROR",
						        message: "Error occured getting players for admin",
		    					status: 200
						   });
	    		});

	});

function adminSnapshotToArray(snapshot){
	var returnArr = [];

	if(!snapshot.exists()) return returnArr;

	snapshot.forEach(function(childSnapshot){

		if(childSnapshot.exists()) {
			var data = {
				"phoneNumber" : childSnapshot.child('/phoneNumber').val(),
				"userName" : childSnapshot.child('/userName').val(),
				"isActive" : childSnapshot.child('/active').val(),
				"type" : childSnapshot.child('/type').val()
			};

			returnArr.push(data);
		}
	});

	return returnArr;
}

exports.adminSettings = 
	functions.https.onRequest((req, res) => {

		if(req.method !== "POST"){
			return res.status(400).send('Please send a POST request');
		}

		var data = {
			"initial_user_amt": req.body.initial_user_amt,
			"trans_amt_commodity": req.body.trans_amt_commodity,
			"trans_amt_currency": req.body.trans_amt_currency,
			"trans_amt_nifty": req.body.trans_amt_nifty
		};

		return admin.database().ref('/ADMINSETTINGS').update(data)
				.then(snapshot=>{

					return res.status(200).json({
					        flag: "SUCCESS",
					        message: "successful update operation",
							status: 200
				    });

				}).catch(exception=>{
				 
					return res.status(200).json({
					        flag: "INTERNAL SERVER ERROR",
					        message: "Error occured while updating records",
							status: 200
				    });
				});

	});
/*
//function to upload file
exports.updateProfile =
	functions.https.onRequest((req, res) => {

	//	cors(req, res, () => {

			if(req.method !== "POST"){
				return res.status(400).send('Please send a POST request');
			}

			var fileName;
			var phoneNumber;
			var userName;
			var userData;
			var type;

			const busboy = new Busboy({ headers: req.headers });
		    let uploadData = null;

		    var IMGURL;

		    busboy.on("file", (fieldname, file, filename, encoding, mimetype) => {
		      console.log("file");	
		      
		      const filepath = path.join(os.tmpdir(), filename);
		      fileName = filename;
		      uploadData = { file: filepath, type: mimetype };
		      file.pipe(fs.createWriteStream(filepath));

		    });

		    busboy.on('field', function(fieldname, val, fieldnameTruncated, valTruncated) {
		    
		       console.log("request body:"+val);

		       if(fieldname === 'phoneNumber') id = val;
		       else if(fieldname === 'userName') experiment = val;
		       else if(fieldname === 'userData') param = val;
		       else if(fieldname === 'type') date = val;

		    });

		    busboy.on("finish", () => {

		    	uploadFile(uploadData.file, phoneNumber, userName, uploadData)
		    		.then( downloadURL => {
	
				    	IMGURL = downloadURL;

				    	return res.status(200).json({
					        flag: "UPLOAD SUCCESS",
					        imgurl: IMGURL,
		    				status: 200
					   });

					}).catch(exception=>{
		    			//console.log("player exception: " + exception);
			    		return res.status(200).json({
							      flag: "UPLOAD ERROR",
							      message: "Error occured while uploading file to bucket",
				    			  status: 200
							   });
			    	});

		    });

		    busboy.end(req.rawBody);//important
	//	});

	});

var uploadFile = (localFile, phoneNumber, userName, uploadData) => {

	var uuid = UUID();

	const bucket = storage.bucket(bucketName);
	bucket.upload(localFile, {
	destination: '/Profile/'+ phoneNumber +'/'+userName + '.jpg',uploadType: "media",
	metadata: { metadata: { contentType: uploadData.type, metadata: { firebaseStorageDownloadTokens: uuid } } } })
			.then((data) => {
 				let file = data[0];
   				var IMGURL = "https://firebasestorage.googleapis.com/v0/b/" + bucket.name + "/o/" + encodeURIComponent(file.name) + "?alt=media&token=" + uuid;

   				return Promise.resolve(IMGURL);

   			}).catch(exception=>{
    			//console.log("player exception: " + exception);
	    		return res.status(200).json({
					        flag: "UPLOAD ERROR",
					        message: "Error occured while uploading file to bucket",
		    				status: 200
					   });
	    	});

}
*/


//set image url
exports.imageUrl = 
	functions.https.onRequest((req, res) => {

		if(req.method !== "POST"){
			return res.status(400).send('Please send a POST request');
		}

		var UPDATES = {};

		UPDATES['/LeaderBoardData/'+req.body.phoneNumber+'/imageUrl'] = (req.body.imageUrl===undefined?null:req.body.imageUrl);
		UPDATES['/Players/'+req.body.phoneNumber+'/imageUrl'] = (req.body.imageUrl===undefined?null:req.body.imageUrl);

		return admin.database().ref().update(UPDATES)
				.then(snapshot=>{
					return res.status(200).json({
							        message: "Successful update.",
					    			status: 200
						   });
				});
	});

//new function to update user
exports.updateuser = functions.https.onRequest((req, res) => {

	if(req.method !== "POST"){
		return res.status(400).send('Please send a POST request');
	}

    const phoneNumber = req.body.phoneNumber;
    const userName = req.body.userName;
    const userData = req.body.userData;
    const type = req.body.type;

    var data = {

    	"phoneNumber" : phoneNumber,
    	"userName" : userName,
    	"active": true,
    	"type" : type,
    	"userData" : userData
    };
            

    return admin.database().ref('/Players').child('/' + phoneNumber).once('value')
    		.then(snapshot=>{
    			//console.log("player once result");
    			
    			if(!snapshot.exists()) {
    				//console.log("username does not exist:" + new Date().getTime());

    				return admin.database().ref('/ADMINSETTINGS').child('/close_reg_time').once('value')
    						.then(snapshot=>{

    							var current_time = new Date().getTime();
    							//console.log("player exception: " + snapshot.val() + ":" + current_time);

    							if(parseInt(current_time) > parseInt(snapshot.val())) {

    								return res.status(200).json({
									        flag: "REGISTRATION_CLOSED",
									        message: "Registation has been closed. Please contact admin.",
					    					status: 200
									   });

    							}else {
    								return addNewuser(req, res);
    							}

    						}).catch(exception=>{
				    			//console.log("player exception: " + exception);
				    			return res.status(200).json({
									        flag: "INTERNAL SERVER ERROR",
									        message: "Error occured while checking player",
					    					status: 200
									   });
				    		});

    			}else {
    				//console.log("player does exist");
    				return updateUser(req, res, snapshot);
    			}

    		}).catch(exception=>{
    			//console.log("player exception: " + exception);
    			return res.status(200).json({
					        flag: "INTERNAL SERVER ERROR",
					        message: "Error occured while checking player",
	    					status: 200
					   });
    		});

});

function addNewuser(req, res) {

	const phoneNumber = req.body.phoneNumber;
    const userName = req.body.userName;
    const userData = req.body.userData;
    const type = req.body.type;

	return admin.database().ref('/UserNames').child('/'+ userName).once('value')
			.then(snapshot => {

				if(snapshot.exists()) {
					
					//console.log("username exist:" + new Date().getTime());

					return res.status(200).json({
					        flag: "USERNAME_EXIST",					       
	    					status: 200
					   });

				}else {

					return admin.database().ref('/ADMINSETTINGS').once('value')
							.then(snapshot=>{

								//console.log("admin settings");

								var data = {

							    	"phoneNumber" : phoneNumber,
							    	"userName" : userName,
							    	"active": true,
							    	"userData" : userData,
							    	"type" : type,
							    	"Account" : initialUserSetUp(snapshot)
							    };

							    var userdata = {
									"phoneNumber" : phoneNumber,
									"userName" : userName,
									"active": true,
									"type" : type
								};

							    var leaderBoardData = {
							    	"phoneNumber" : phoneNumber,
							    	"userName" : userName,
							    	"start_balance" : snapshot.child('/initial_user_amt').val(),
							    	"avail_balance" : snapshot.child('/initial_user_amt').val()
							    }

							    var UPDATES = {};

							    UPDATES['/Players/'+phoneNumber] = data;
							    UPDATES['/LeaderBoardData/'+phoneNumber] = leaderBoardData;
							    UPDATES['/UserNames/'+userName] = userdata;

							    //run these two parallelly
							    return admin.database().ref().update(UPDATES)
							    		.then(snapshot=> {

							    			//console.log("player added");

							    			return res.status(200).json({
							          					flag: "USER_ADDED",
							            				status: 200
							           	    		});

							    		}).catch(exception=>{
							    			//console.log("player exception");
							    			return res.status(200).json({
												        flag: "INTERNAL SERVER ERROR",
												        message: "Error occured while pushing player",
								    					status: 200
												   });
							    		});

							}).catch(exception=>{
				    			console.log("player exception");
				    			return res.status(200).json({
									        flag: "INTERNAL SERVER ERROR",
									        message: "Error occured while geting admin settings",
					    					status: 200
									   });
				    		});

				}

			}).catch(exception=>{
    			//console.log("player exception");
    			return res.status(200).json({
					        flag: "INTERNAL SERVER ERROR",
					        message: "Error occured while checking user names",
	    					status: 200
					   });
    		});
}

function updateUser(req, res, snapshot) {

	var dbref = admin.database();

	const phoneNumber = req.body.phoneNumber;
    const userName = req.body.userName;
    const userData = req.body.userData;
    const type = req.body.type;

    var data = {

    	"phoneNumber" : phoneNumber,
    	"userName" : userName,
    	"active": true,
    	"type" : type,
    	"userData" : userData
    };

    var prevUserName;

    //no need to check here if account exist. it will be created if account does not exist while fetching account
    //if active exist the except account everythin will definately exist

    if(snapshot.child('/active').exists()) {

    	if(snapshot.child('/active').val() === true) {

    		if(snapshot.child('/userName').val() === userName) {
    				
    			return dbref.ref('/Players').child('/'+phoneNumber).update(data)
    					.then(snapshot=>{

    						return res.status(200).json({
					          			flag: "USER_ADDED",
					          			message: "player updated, same username",
					            		status: 200
					           	    });

    					}).catch(exception=>{
				    		//console.log("player updatin exception: " + exception);
				    		return res.status(200).json({
									    flag: "INTERNAL SERVER ERROR",
									    message: "Error occured while updating existing player info",
					    				status: 200
								   });
				    	});

    			}else {

    				prevUserName = snapshot.child('/userName').val();

    				//console.log('different username');

    				return dbref.ref('/UserNames').child('/'+userName).once('value')
    						.then(snapshot=>{

    							if(snapshot.exists()) {
    								return res.status(200).json({
								        flag: "USERNAME_EXIST",
				    					status: 200
								   });

    							}else{

    								return dbref.ref('/Players').child('/'+phoneNumber).update(data)
			    						.then(snapshot=>{

			    							var data = {

										    	"phoneNumber" : phoneNumber,
										    	"userName" : userName,
										    	"active": true,
										    	"type" : type
										    };

							    			return admin.database().ref('/UserNames').child('/'+userName).update(data)
							    					.then(snapshot=>{

							    						//console.log("username added");

							    						return dbref.ref('/UserNames').child('/'+prevUserName).remove()
							    								.then(snapshot=>{

							    									return res.status(200).json({
										          						flag: "USER_ADDED",
										          						message: "player added, different username",
										            					status: 200
										           	    			});

							    								}).catch(exception=>{
													    			//console.log("user name exception");
													    			return res.status(200).json({
																		        flag: "USER_ADDED",
																		        message: "Error occured while removing old username",
														    					status: 200
																		   });
													    		});							 

							    					}).catch(exception=>{
										    			//console.log("user name exception");
										    			return res.status(200).json({
															        flag: "INTERNAL SERVER ERROR",
															        message: "Error occured while pushing username",
											    					status: 200
															   });
										    		});

			    						}).catch(exception=>{
							    			//console.log("player updatin exception: " + exception);
							    			return res.status(200).json({
												        flag: "INTERNAL SERVER ERROR",
												        message: "Error occured while updating existing player info with different username",
								    					status: 200
												   });
							    		});

    							}
    					});

    			}

    	}else {

    		return res.status(200).json({
						flag: "USER_DISABLED",
						message: "User has been disabled by the admin",
		    			status: 200
				   });
    	}

    }else {

    	return dbref.ref('/UserNames').child('/'+userName).once('value')
    			.then(snapshot=>{
    				if(snapshot.exists()) {
    					return res.status(200).json({
							    	flag: "USERNAME_EXIST",
				        			status: 200
								});

    				}else{

    					return dbref.ref('/Players').child('/'+phoneNumber).update(data)
			    				.then(snapshot=>{

			    					return admin.database().ref('/UserNames').child('/'+userName).update(data)
							    				.then(snapshot=>{

							    					//console.log("username added");

							    					return res.status(200).json({
							          					flag: "USER_ADDED",
							          					message: "player added, no username",
							            				status: 200
							           	    		});

							    				}).catch(exception=>{
										    		//console.log("user name exception");
										    		return res.status(200).json({
														        flag: "INTERNAL SERVER ERROR",
														        message: "Error occured while pushing username",
											   					status: 200
														   });
										    		});

			    				}).catch(exception=>{
							    	//console.log("player updatin exception: " + exception);
							    	return res.status(200).json({
											    flag: "INTERNAL SERVER ERROR",
											    message: "Error occured while updating existing player info",
								    			status: 200
										   });
							   	});

    					}
    		});

    }

}

//function to get username
exports.userNameData = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

    var phoneNumber = req.query.phoneNumber;
    
    return admin.database().ref('/Players').child('/' + phoneNumber).child('/userData').once('value')
   			.then(snapshot=>{

   				if(snapshot.exists()) {
		       		return res.json({
		       			"userData" : snapshot.val(),
		       			"status" : 200 
		       		});   

		       	}else {
		      		return res.json({
		       			"status" : 200 
		       		});   
		       	}

   			}).catch(exception=>{
    			//console.log("player exception");
    			return res.status(200).json({
					        flag: "INTERNAL SERVER ERROR",
					        message: "Error occured while getting user name data",
	    					status: 200
					   });
    		});

});    


exports.userStatus = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	var phoneNumber = req.query.phoneNumber;

	return admin.database().ref('/Players').child('/'+ phoneNumber).child('/active').once('value')
			.then(snapshot=>{

				return res.json({
       				"isActive" : snapshot.val(),
       				"status" : 200 
       			});

			}).catch(exception=>{
    			//console.log("player exception");
    			return res.status(200).json({
					        flag: "INTERNAL SERVER ERROR",
					        message: "Error occured while checking user names",
	    					status: 200
					   });
    		});

});


//called in data fragment
exports.userAccount = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	if(!isDayValidForTransaction(req.query.item_type)) {

		return res.json({
					"flag" : "MARKET CLOSED",
			   		"message" : 'Market is closed. You transaction cannot be processed right now. Please try again later.',
			       	"status" : 200 
			    });  
	}

	var phoneNumber = req.query.phoneNumber;

	return admin.database().ref('/Players').child('/'+ phoneNumber).child('/Account').once('value')
			.then(snapshot=>{

				if(snapshot.exists()) {

					return res.json({
			       			"data" : snapshot.val(),
			       			"timestamp" : new Date().getTime(),
			       			"status" : 200 
			       		}); 

				}else {
					//create user account here

					return admin.database().ref('/ADMINSETTINGS').once('value')
							.then(snapshot=>{

								var initial_data = initialUserSetUp(snapshot);

								return admin.database().ref('/Players').child('/'+phoneNumber).child('/Account').update(initialUserSetUp(snapshot))
										.then(snapshot=>{

											return res.json({
									       			"message" : "no data",
									       			"data": initial_data,
									       			"timestamp" : new Date().getTime(),
									       			"status" : 200 
									       	});  
										});
							});
				}

			}).catch(exception=>{
				console.log(exception);
    			return res.status(200).json({
					        flag: "INTERNAL SERVER ERROR",
					        message: "Error occured while getting player account",
	    					status: 200
					   });
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

//called in data fragment, sell and purchase activity
exports.userTxnAccount = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	if(!isDayValidForTransaction(req.query.item_type)) {

		return res.json({
					"flag" : "MARKET CLOSED",
			   		"message" : 'Market is closed. You transaction cannot be processed right now. Please try again later.',
			       	"status" : 200 
			    });  
	}

	var phoneNumber = req.query.phoneNumber;

	var admin_settings = admin.database().ref('/ADMINSETTINGS').once('value');
	var account = admin.database().ref('/Players').child('/'+phoneNumber).child('/Account').once('value');

	var promises = [];

	promises.push(admin_settings);
	promises.push(account);

	return Promise.all(promises)
			.then(snapshot=>{

				var settings;
				var user_account;

				snapshot.forEach(childSnapshot=>{

					if(childSnapshot.key === "ADMINSETTINGS") {
						settings = childSnapshot.val();
					}else {
						user_account = childSnapshot.val();
					}

				});

				var data = {
					"admin_settings"  : settings, 
					"Account" : user_account
				};

				return res.json({
			   		"data" : data,
			   		"timestamp" : new Date().getTime(),
			       	"status" : 200 
			    });  

			}).catch(exception=>{
    			return res.status(200).json({
					        flag: "INTERNAL SERVER ERROR",
					        message: "Error occured while getting user txn account promises",
	    					status: 200
					   });
    		});

});

//depricated
exports.transaction = functions.https.onRequest((req, res) => {

	if(req.method !== "POST"){
		return res.status(400).send('Please send a POST request');
	}

	var phoneNumber = req.body.phoneNumber;
	var Account = req.body.Account;
	var type = req.body.item_type;

	console.log('txn not aborted');

    var db_ref = admin.database().ref('/Players').child('/'+phoneNumber).child('/Account');

    return db_ref.update(Account).then((snapshot)=>{
    	return res.json({
	   		"message" : "transaction successful",
	       	"status" : 200 
	    }); 
    });
});

//new function
exports.txn = functions.https.onRequest((req, res) => {

	if(req.method !== "POST"){
		return res.status(400).send('Please send a POST request');
	}

	var phoneNumber = req.body.phoneNumber;
	var Account = req.body.Account;
	var type = req.body.item_type;
	var leaderBoardData = req.body.leaderBoardData;

	var UPDATES = {};

	var Portfolio = {
		[leaderBoardData.txn_id] : leaderBoardData.txnData
	};


	var userData = {
		"avail_balance" : leaderBoardData.avail_balance,
		"phoneNumber" : phoneNumber,
		"userName" : leaderBoardData.userName,
		"start_balance" : leaderBoardData.start_balance,
	};

	UPDATES['/Players/'+phoneNumber+'/Account'] = Account;
	UPDATES['/LeaderBoardData/'+phoneNumber+'/Portfolio/'+leaderBoardData.txn_id] = (leaderBoardData.txnData===undefined?null:leaderBoardData.txnData); 
	UPDATES['/LeaderBoardData/'+phoneNumber+'/avail_balance'] = leaderBoardData.avail_balance;
	UPDATES['/LeaderBoardData/'+phoneNumber+'/phoneNumber'] = phoneNumber;
	UPDATES['/LeaderBoardData/'+phoneNumber+'/userName'] = leaderBoardData.userName;
	UPDATES['/LeaderBoardData/'+phoneNumber+'/start_balance'] = leaderBoardData.start_balance;
	//UPDATES['/LeaderBoardData/'+phoneNumber+'/imageUrl'] = (leaderBoardData.imageUrl===undefined?null:leaderBoardData.imageUrl);
	//console.log('txn not aborted');

    var db_ref = admin.database().ref();

    return db_ref.update(UPDATES).then((snapshot)=>{
    	return res.json({
	   		"message" : "transaction successful",
	       	"status" : 200 
	    }); 
    });
});

exports.userinfo = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	var phoneNumber = req.query.phoneNumber;

	return admin.database().ref('/Players').child("/"+phoneNumber).once('value')
			.then(snapshot=>{

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
				"imageUrl" : snapshot.val().imageUrl,
				"phoneNumber": snapshot.val().phoneNumber,
				"userName": snapshot.val().userName,
				"active": snapshot.val().active,
				"Account": Account
			};

			return res.json({
		   		"data" : data,
		       	"status" : 200 
		    });  
		
	}).catch(exception=>{
    			return res.status(200).json({
			        flag: "INTERNAL SERVER ERROR",
			        message: "Error occured while getting player account",
	    			status: 200
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


exports.boughtfragmentinfo = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	var phoneNumber = req.query.phoneNumber;

	var acc_bal = admin.database().ref('/Players').child('/'+phoneNumber).child('/Account').child('/avail_balance').once('value');
	var admin_settings = admin.database().ref('/ADMINSETTINGS').once('value');
	var bought_items = admin.database().ref('/Players').child("/"+phoneNumber).child('/Account').child('/stocks_list').child('/bought_items').once('value');
	 
	var promises = [];
	promises.push(acc_bal);
	promises.push(admin_settings);
	promises.push(bought_items);

	return Promise.all(promises)
			.then(snapshot=> {

				var bal;
				var settings;
				var items;

				snapshot.forEach(childSnapshot=>{
					//console.log(""+childSnapshot.val());

					if(childSnapshot.key === "avail_balance") {
						bal = childSnapshot.val();
					}else if(childSnapshot.key === "ADMINSETTINGS") {
						settings = childSnapshot.val();
					}else {
						items = {

							"index" : stockstoArray(childSnapshot.child('/index')),
							"commodity" : stockstoArray(childSnapshot.child('/commodity')),
							"currency" : stockstoArray(childSnapshot.child('/currency')),
							"fixed_deposit" : stockstoArray(childSnapshot.child('/fixed_deposit'))
						};
					}

				});

				var data = {

					"avail_balance" : bal,
					"admin_settings" : settings,
					"bought_items" : items
				};

		return res.json({
	   		"data" : data,
	       	"status" : 200 
	    });  
		
	}).catch(exception=>{
		//console.log("exception: "+exception+"");
    			return res.status(200).json({
					        flag: "INTERNAL SERVER ERROR",
					        message: "Error occured while resolving all bought fagment promises",
	    					status: 200
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
			"currency" : stockstoArray(snapshot.child('/currency')),
			"fixed_deposit" : stockstoArray(snapshot.child('/fixed_deposit'))
		};

		return res.json({
	   		"data" : data,
	       	"status" : 200 
	    });  
		
	});

});

//depricated
exports.leaderboard = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	return admin.database().ref('Players').once('value', function(snapshot) {
		return res.json({
	   		"data" : formatLeaderboard(snapshot),
	       	"status" : 200 
	    });  
	});

});

//new leader board function(reduced size)
exports.standings = functions.https.onRequest((req, res) => {

	if(req.method !== "GET"){
		return res.status(400).send('Please send a GET request');
	}

	return admin.database().ref('LeaderBoardData').once('value')
			.then(snapshot=>{

				return res.json({
			   		"data" : formatStandings(snapshot),
			       	"status" : 200 
			    });  

			}).catch(exception=>{
    			//console.log("player exception");
    			return res.status(200).json({
					        flag: "INTERNAL SERVER ERROR",
					        message: "Error occured while getting standings",
	    					status: 200
					   });
    		});

});

//get daily winner
exports.dailyleaderboard = 
	functions.https.onRequest((req, res)=>{

		var dailyLeaderBoard = admin.database().ref('/DailyData').child('/leaderboard').once('value');
		var Players = admin.database().ref('/Players').once('value');

		var promises = [];

		promises.push(dailyLeaderBoard);
		promises.push(Players);

		return Promise.all(promises)
				.then(snapshot=>{

					var prevBoardData;
					var todaysBoardData = {};
					var players;
					var winner = {};

					snapshot.forEach(function(childSnapshot){

						if(childSnapshot.key === "Players") {
							players = childSnapshot;
						}else {
							prevBoardData = childSnapshot;
						}
					});
					
					var temp = -Number.MAX_VALUE;
					var ALL_UPDATES = {};
					var LEADERBOAD_UPDATES = {};
					var current_timestamp = new Date().getTime();
					var listWinner = [];

					players.forEach(function(childSnapshot){


						//update fixed deposit function (all updates wrong way of doing)****************************************************************************
						/*var updt = updateFixedDeposit(childSnapshot, current_timestamp);

						if(updt !== null && updt !== undefined) {
							ALL_UPDATES[childSnapshot.key] = updt; 
						}*/

						var avail_bal  = getValue(childSnapshot.child('/Account').child('/avail_balance'));
					    var change = getValue(childSnapshot.child('/Account').child('/change'));
					    var pchange = getValue(childSnapshot.child('/Account').child('/percentchange'));
					    var shares_price = getValue(childSnapshot.child('/Account').child('/shares_price'));
					    var start_balance = getValue(childSnapshot.child('/Account').child('/start_balance'));

					    var starttime =  0;
					    var timestamp =  0;
					    var lastupdate =  0;
					    var nextupdate =  0;
					    var firstupdate =  0;

					    var SI_CHANGE = 0.0;

					    var fd_ref = childSnapshot.child('/Account').child('/stocks_list').child('/bought_items').child('/fixed_deposit');

					    try {

						    if(fd_ref.exists()) {

							    fd_ref.forEach(function(innerchildSnapshot){

							    	//console.log(childSnapshot.val());

							    	try{

							    		starttime =  getLongValue(innerchildSnapshot.child('/starttime'));
							    		timestamp =  getLongValue(innerchildSnapshot.child('/timestamp'));
							    		lastupdate =  getLongValue(innerchildSnapshot.child('/lastupdate'));
							    		nextupdate =  getLongValue(innerchildSnapshot.child('/nextupdate'));
							    		firstupdate =  getLongValue(innerchildSnapshot.child('/firstupdate'));

							    		if(getTotalDayCount(current_timestamp, lastupdate) > 0) {
								    		
								    		//console.log("phoneNumber: "+ childSnapshot.key );

								    		var prev_currentval = getValue(innerchildSnapshot.child('/current_value'));

								    		var SI = getSimpleInterest(getTotalDayCount(current_timestamp, starttime), innerchildSnapshot);
								    		var current_value = getValue(innerchildSnapshot.child('/investment')) + SI;

								    		SI_CHANGE += (current_value - prev_currentval);

								    		lastupdate = getLastUpdate(current_timestamp);
								    		nextupdate = getNextUpdate();

								    		//TODO: set updates here
								    		ALL_UPDATES['/'+childSnapshot.key+'/Account/stocks_list/bought_items/fixed_deposit/'+innerchildSnapshot.key+'/lastupdate'] = lastupdate;
								    		ALL_UPDATES['/'+childSnapshot.key+'/Account/stocks_list/bought_items/fixed_deposit/'+innerchildSnapshot.key+'/nextupdate'] = nextupdate;
								    		ALL_UPDATES['/'+childSnapshot.key+'/Account/stocks_list/bought_items/fixed_deposit/'+innerchildSnapshot.key+'/current_value'] = current_value;
								    		LEADERBOAD_UPDATES['/'+childSnapshot.key+'/Portfolio/'+innerchildSnapshot.key+'/current_value'] = current_value;
							    		}

							    	}catch(err){
							    		console.log("Error occured: " +err)
							    		//continue;
							    	}

							    });

							    /* //dont update it here. It will be updated once user sells his fd
							    if(SI_CHANGE > 1) {

								    avail_bal += SI_CHANGE;
								    change += SI_CHANGE;

								    pchange = ((avail_bal + shares_price - start_balance) / start_balance ) * 100;

								    ALL_UPDATES['/'+childSnapshot.key+'/Account/avail_balance'] = avail_bal;
								    ALL_UPDATES['/'+childSnapshot.key+'/Account/change'] = change;
								    ALL_UPDATES['/'+childSnapshot.key+'/pchange'] = pchange;
								}
								*/

							}

						}catch(err) {
							console.log("Error occured:"+childSnapshot.key+":"+err);
						}

						//******************************************ALL UPDATES FUNCTION***********************************************************************

						try{

							var daychange = getValue(childSnapshot.child('/Account').child('/change')) 
								- getValue(prevBoardData.child('/'+childSnapshot.key).child('/change'));

							var data = {
								"phoneNumber": childSnapshot.key,
								"change" : getValue(childSnapshot.child('/Account').child('/change'))
							};
								
							todaysBoardData[childSnapshot.key] = data;

							if(parseFloat(daychange) > parseFloat(temp)){
								
								listWinner = [];
								temp = parseFloat(daychange);
								winner = {
									"phoneNumber" : childSnapshot.key,
									"daychange" : daychange
								};
								listWinner.push(winner);

							}else if(parseFloat(daychange) === parseFloat(temp)) {
								
								winner = {
									"phoneNumber" : childSnapshot.key,
									"daychange" : daychange
								};
								listWinner.push(winner);

							}	

						}catch(err) {
							console.log('exception:'+err);
						}

					});

					
					var date = new Date(current_timestamp + 5*60*60*1000 + 30*60*1000);
					var stringdate = date.getDate() + '-' + (date.getMonth()+1) + '-' + date.getFullYear();

					var pushUpdates = admin.database().ref('/Players').update(ALL_UPDATES);
					var leaderboardUpdates = admin.database().ref('/LeaderBoardData').update(LEADERBOAD_UPDATES);
					var lastupdate = admin.database().ref('/DailyData').child('/lastupdate').set(date.getTime());
					var currentstandings = admin.database().ref('/DailyData').child('/leaderboard').update(todaysBoardData);
					var todaywinner = admin.database().ref('/DailyData').child('/Winner').child('/'+stringdate).set(listWinner);

					var innerpromises = [];

					innerpromises.push(pushUpdates);
					innerpromises.push(leaderboardUpdates);
					innerpromises.push(lastupdate);
					innerpromises.push(currentstandings);
					innerpromises.push(winner);

					return Promise.all(innerpromises)
							.then(snapshot=>{

								return res.status(200).json({
									        message: "Daily update successful",
					    					status: 200,
					    					timestamp : getLastUpdate(new Date().getTime()),
					    					nextupdate: getNextUpdate()
									   });

							}).catch(exception=>{
								console.log('exception:'+exception);
				    			return res.status(200).json({
									        flag: "INTERNAL SERVER ERROR",
									        message: "Error occured while upadating daily records",
					    					status: 200
									   });
				    		});

				}).catch(exception=>{
					console.log('exception:'+exception);
		    			return res.status(200).json({
							        flag: "INTERNAL SERVER ERROR",
							        message: "Error occured while fetching daily records",
			    					status: 200
							   });
		    		});
	});


//Utiltiy function for fixed deposit(depricated)
function updateFixedDeposit(snapshot, current_timestamp) {

	//console.log('phoneNumber: '+ snapshot.key);

	var updates = {};

	var avail_bal  = getValue(snapshot.child('/Account').child('/avail_balance'));
    var change = getValue(snapshot.child('/Account').child('/change'));
    var pchange = getValue(snapshot.child('/Account').child('/percentchange'));
    var shares_price = getValue(snapshot.child('/Account').child('/shares_price'));
    var start_balance = getValue(snapshot.child('/Account').child('/start_balance'));

    var starttime =  0;
    var timestamp =  0;
    var lastupdate =  0;
    var nextupdate =  0;
    var firstupdate =  0;

    var SI_CHANGE = 0.0;

    var fd_ref = snapshot.child('/Account').child('/stocks_list').child('/bought_items').child('/fixed_deposit');

    if(!fd_ref.exists()) return null; 

    fd_ref.forEach(function(childSnapshot){

    	//console.log(childSnapshot.val());

    	try{

    		var prev_currentval = getValue(childSnapshot.child('/current_value'));

    		starttime =  getLongValue(childSnapshot.child('/starttime'));
    		timestamp =  getLongValue(childSnapshot.child('/timestamp'));
    		lastupdate =  getLongValue(childSnapshot.child('/lastupdate'));
    		nextupdate =  getLongValue(childSnapshot.child('/nextupdate'));
    		firstupdate =  getLongValue(childSnapshot.child('/firstupdate'));

    		if(getTotalDayCount(current_timestamp, lastupdate) > 0) {
	    		
	    		//console.log("phoneNumber: "+ snapshot.key );

	    		var SI = getSimpleInterest(getTotalDayCount(current_timestamp, starttime), childSnapshot);
	    		var current_value = getValue(childSnapshot.child('/investment')) + SI;

	    		SI_CHANGE += (current_value - prev_currentval);

	    		lastupdate = getLastUpdate(current_timestamp);
	    		nextupdate = getNextUpdate();

	    		//TODO: set updates here
	    		updates['/Account/stocks_list/bought_items/fixed_deposit/'+childSnapshot.key+'/lastupdate'] = lastupdate;
	    		updates['/Account/stocks_list/bought_items/fixed_deposit/'+childSnapshot.key+'/nextupdate'] = nextupdate;
	    		updates['/Account/stocks_list/bought_items/fixed_deposit/'+childSnapshot.key+'/current_value'] = current_value;

    		}

    	}catch(err){
    		console.log("Error occured: " +err)
    		//continue;
    	}

    });

    if(SI_CHANGE < 1) return null;

    avail_bal += SI_CHANGE;
    change += SI_CHANGE;

    pchange = ((avail_bal + shares_price - start_balance) / start_balance ) * 100;

    updates['/Account/avail_bal'] = avail_bal;
    updates['/Account/change'] = change;
    updates['/pchange'] = pchange;

    return updates;

}

function getSimpleInterest(noOfDays, txn_fd) {

    SI_F7 = ( getValue(txn_fd.child('/investment')) * (6.0/365) * noOfDays) / 100;

    SI_S7 = ( getValue(txn_fd.child('/investment')) * (6.25/365) * ((noOfDays-7)>=0?(noOfDays-7):0)) / 100;

    SI_T15 = ( getValue(txn_fd.child('/investment')) * (6.5/365) * ((noOfDays-15)>=0?(noOfDays-15):0)) / 100;

    //console.log("INTEREST RATES", ""+SI_F7 + " : " + SI_S7 + " : " + SI_T15);

    return (SI_F7 + SI_S7 + SI_T15);
}

function getTotalDayCount(starttime, endtime) {

	//console.log("starttime:" + starttime + " endtime:"+endtime);

    msdiff = starttime - endtime;

    if(msdiff < 0) return 0;

    daysDiff = Math.floor(msdiff/(1000*60*60*24));

    //console.log("days diff", ""+daysDiff+ ":"+ msdiff);

    return daysDiff;
}


function getLastUpdate(timestamp){

	var date = new Date(timestamp + 5*60*60*1000 + 30*60*1000);
	//console.log(date);
	
	var d;
	var m;
	var y = date.getFullYear();

	if(parseInt(date.getDate()) < 10) d = "0"+date.getDate();
	else d = date.getDate(); 

	if(parseInt((date.getMonth()+1)) < 10) m = "0"+(date.getMonth()+1);
	else m = date.getMonth()+1; 

	var stringdate = y + "/" + m + "/" + d +" 00:00:00+05:30";

	//console.log("string date to timestamp " + stringdate+":" + new Date(stringdate).getTime());

	return new Date(stringdate).getTime();
}

function getNextUpdate() {
	return getLastUpdate(getCurrentDayStartTimesStamp() + 24*60*60*1000);
}

function getCurrentDayStartTimesStamp(){
	return new Date().getTime();
}

//supporting functions
function getValue(snapshot){

	if(snapshot.exists()) {
		//console.log(snapshot.val());
		return parseFloat(snapshot.val());
	}else {
		return 0.0;
	}
}

//supporting functions
function getLongValue(snapshot){

	if(snapshot.exists()) {
		//console.log(snapshot.val());
		return parseInt(snapshot.val());
	}else {
		return 0;
	}
}

//supporting functions
function formatLeaderboard(snapshot) {

	var returnArr = [];
    //console.log(snapshot);

    if(!snapshot.exists()) return returnArr;

    snapshot.forEach(function(childSnapshot) {
       
      // console.log(childSnapshot.val().phoneNumber);

       		var data = {
       			"phoneNumber" : getPhoneNumber(childSnapshot),
       			"userName" : getUserName(childSnapshot),
       			"avail_balance" : getAvailBal(childSnapshot.child('/Account')),
       			"start_balance" : getStartBal(childSnapshot.child('/Account')),
       			"percentchange" : getPerChange(childSnapshot.child('/Account')),
       			"change" : getChange(childSnapshot.child('/Account')),
       			"commodity" : formatstocksList(childSnapshot.child('/Account').child('/stocks_list').child('/bought_items').child('/commodity')),
	   			"currency" : formatstocksList(childSnapshot.child('/Account').child('/stocks_list').child('/bought_items').child('/currency')),
	   			"fixed_deposit" : formatstocksList(childSnapshot.child('/Account').child('/stocks_list').child('/bought_items').child('/fixed_deposit')),
	   			"index" : formatstocksList(childSnapshot.child('/Account').child('/stocks_list').child('/bought_items').child('/index'))
       		};

        	returnArr.push(data);
      
    });

    return returnArr;

}

function formatStandings(snapshot) {

	var returnArr = [];
    //console.log(snapshot);

    if(!snapshot.exists()) return returnArr;

    snapshot.forEach(function(childSnapshot) {
       
      // console.log(childSnapshot.val().phoneNumber);

       		var data = {
       			"imageUrl" : childSnapshot.child('/imageUrl').val(),
       			"phoneNumber" : getPhoneNumber(childSnapshot),
       			"userName" : getUserName(childSnapshot),
       			"avail_balance" : getAvailBal(childSnapshot),
       			"start_balance" : getStartBal(childSnapshot),
       			"Portfolio" : toArray(childSnapshot.child('/Portfolio'))
       		};

        	returnArr.push(data);
      
    });

    return returnArr;

}

function getPhoneNumber(snapshot) {
	if(!snapshot.exists()) return "dummy";
	return snapshot.val().phoneNumber;
}

function getUserName(snapshot) {
	if(!snapshot.exists()) return "dummy";
	return snapshot.val().userName;
}

function getChange(snapshot) {
	if(!snapshot.exists()) return "0.0";
	return snapshot.val().change;
}

function getPerChange(snapshot) {
	if(!snapshot.exists()) return "0.0";
	return snapshot.val().percentchange;
}

function getAvailBal(snapshot) {
	if(!snapshot.exists()) return "1000000";
	return snapshot.val().avail_balance;
}

function getStartBal(snapshot) {
	if(!snapshot.exists()) return "1000000";
	return snapshot.val().start_balance;
}

function formatstocksList(snapshot) {
    var returnArr = [];
    //console.log(snapshot);

    if(!snapshot.exists()) return returnArr;

    var data = {};

    snapshot.forEach(function(childSnapshot) {
       
       	if(childSnapshot.key.indexOf("txn") !== -1) {
       		if(childSnapshot.val().id.indexOf("FD") === 0) {
       			data = {
	       			"id" : childSnapshot.val().id,
	       			"qty" : childSnapshot.val().qty,
	       			"total_amount" : childSnapshot.val().total_amount,
	       			"current_value" : childSnapshot.val().current_value
	       		};
       		}else {
	       		data = {
	       			"id" : childSnapshot.val().id,
	       			"qty" : childSnapshot.val().qty,
	       			"total_amount" : childSnapshot.val().total_amount
	       		};
       		}
        	returnArr.push(data);
       	}
      
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
    //console.log(snapshot);

    if(!snapshot.exists()) return returnArr;

    snapshot.forEach(function(childSnapshot) {
       
        	returnArr.push(childSnapshot.val());
      
    });

    return returnArr;
}

function stockstoArray(snapshot) {
    var returnArr = [];
    //console.log(snapshot);

    if(!snapshot.exists()) return returnArr;

    snapshot.forEach(function(childSnapshot) {
       
       	if(childSnapshot.key.indexOf("txn") !== -1) {
        	returnArr.push(childSnapshot.val());
       	}
      
    });

    return returnArr;
}

function isDayValidForTransaction(type) {

	var current_date = new Date();
	var current_timestamp = current_date.getTime();

	var close_reg_time = 1550601000000;
	//Registration closed
	if(current_timestamp < close_reg_time) {
		return false;
	}

	var registrationopen = ["2019/01/17", "2019/01/18", "2019/01/19", "2019/01/20"];
	var holidays = ["2019/01/26", "2019/01/27", "2019/02/02", "2019/02/03", "2019/02/09", "2019/02/10", "2019/02/16", "2019/02/17"];

	if(registrationopen.indexOf(getISTtimeString(current_date)) > -1) {

		return false;
	}

	if(holidays.indexOf(getISTtimeString(current_date)) > -1) {

		return false;
	}

	switch(type.toUpperCase()) {

		case 'NIFTY' :

			if(parseInt(current_timestamp) < parseInt(getNiftyLowerLimit(current_timestamp)) 
				|| parseInt(current_timestamp) > parseInt(getNiftyUpperLimit(current_timestamp)) ){

				return false;
			} 
		break;

		case 'COMMODITY' : 

			if(parseInt(current_timestamp) < parseInt(getCurrencyLowerLimit(current_timestamp)) 
				|| parseInt(current_timestamp) > parseInt(getCommodityUpperLimit(current_timestamp)) ){

				return false;
			} 
		break;

		case 'CURRENCY' : 

			if(parseInt(current_timestamp) < parseInt(getCurrencyLowerLimit(current_timestamp)) 
				|| parseInt(current_timestamp) > parseInt(getCurrencyUpperLimit(current_timestamp)) ){

				return false;
			} 
		break;

		case 'FIXED_DEPOSIT' : 

			if(parseInt(current_timestamp) < parseInt(getFdLowerLimit(current_timestamp)) 
				|| parseInt(current_timestamp) > parseInt(getFdUpperLimit(current_timestamp)) ){

				return false;
			} 

		break;
	}

	return true;
}

function getISTtimeString(current_date){

	var date = new Date(current_date.getTime() + 5*60*60*1000 + 30*60*1000);
	//console.log(date);
	
	var d;
	var m;
	var y = date.getFullYear();

	if(parseInt(date.getDate()) < 10) d = "0"+date.getDate();
	else d = date.getDate(); 

	if(parseInt((date.getMonth()+1)) < 10) m = "0"+(date.getMonth()+1);
	else m = date.getMonth()+1; 

	var stringdate = y + "/" + m + "/" + d;

	//console.log("string date to timestamp: " + stringdate + ": date :" + date);

	return stringdate;
}

//lower limit for transaction
function getNiftyLowerLimit(timestamp){

	var date = new Date(timestamp + 5*60*60*1000 + 30*60*1000);
	//console.log(date);
	
	var d;
	var m;
	var y = date.getFullYear();

	if(parseInt(date.getDate()) < 10) d = "0"+date.getDate();
	else d = date.getDate(); 

	if(parseInt((date.getMonth()+1)) < 10) m = "0"+(date.getMonth()+1);
	else m = date.getMonth()+1; 

	var stringdate = y + "/" + m + "/" + d +" 09:15:00+05:30";

	//console.log("string date to timestamp " + stringdate+":" + new Date(stringdate).getTime());

	return new Date(stringdate).getTime();
}

//upper limit for transaction
function getNiftyUpperLimit(timestamp){

	var date = new Date(timestamp + 5*60*60*1000 + 30*60*1000);
	//console.log(date);
	
	var d;
	var m;
	var y = date.getFullYear();

	if(parseInt(date.getDate()) < 10) d = "0"+date.getDate();
	else d = date.getDate(); 

	if(parseInt((date.getMonth()+1)) < 10) m = "0"+(date.getMonth()+1);
	else m = date.getMonth()+1; 

	var stringdate = y + "/" + m + "/" + d +" 15:30:00+05:30";

	//console.log("string date to timestamp " + stringdate+":" + new Date(stringdate).getTime());

	return new Date(stringdate).getTime();
}

//lower limit for transaction
function getCommodityLowerLimit(timestamp){

	var date = new Date(timestamp  + 5*60*60*1000 + 30*60*1000);
	//console.log(date);
	
	var d;
	var m;
	var y = date.getFullYear();

	if(parseInt(date.getDate()) < 10) d = "0"+date.getDate();
	else d = date.getDate(); 

	if(parseInt((date.getMonth()+1)) < 10) m = "0"+(date.getMonth()+1);
	else m = date.getMonth()+1; 

	var stringdate = y + "/" + m + "/" + d +" 09:00:00+05:30";

	//console.log("string date to timestamp " + stringdate+":" + new Date(stringdate).getTime());

	return new Date(stringdate).getTime();
}

//upper limit for transaction
function getCommodityUpperLimit(timestamp){

	var date = new Date(timestamp + 5*60*60*1000 + 30*60*1000);
	//console.log(date);
	
	var d;
	var m;
	var y = date.getFullYear();

	if(parseInt(date.getDate()) < 10) d = "0"+date.getDate();
	else d = date.getDate(); 

	if(parseInt((date.getMonth()+1)) < 10) m = "0"+(date.getMonth()+1);
	else m = date.getMonth()+1; 

	var stringdate = y + "/" + m + "/" + d +" 23:55:00+05:30";

	//console.log("string date to timestamp " + stringdate+":" + new Date(stringdate).getTime());

	return new Date(stringdate).getTime();
}

//lower limit for transaction
function getCurrencyLowerLimit(timestamp){

	var date = new Date(timestamp + 5*60*60*1000 + 30*60*1000);
	//console.log(date);
	
	var d;
	var m;
	var y = date.getFullYear();

	if(parseInt(date.getDate()) < 10) d = "0"+date.getDate();
	else d = date.getDate(); 

	if(parseInt((date.getMonth()+1)) < 10) m = "0"+(date.getMonth()+1);
	else m = date.getMonth()+1; 

	var stringdate = y + "/" + m + "/" + d +" 09:00:00+05:30";

	//console.log("string date to timestamp " + stringdate+":" + new Date(stringdate).getTime());

	return new Date(stringdate).getTime();
}

//upper limit for transaction
function getCurrencyUpperLimit(timestamp){

	var date = new Date(timestamp + 5*60*60*1000 + 30*60*1000);
	//console.log(date);
	
	var d;
	var m;
	var y = date.getFullYear();

	if(parseInt(date.getDate()) < 10) d = "0"+date.getDate();
	else d = date.getDate(); 

	if(parseInt((date.getMonth()+1)) < 10) m = "0"+(date.getMonth()+1);
	else m = date.getMonth()+1; 

	var stringdate = y + "/" + m + "/" + d +" 17:00:00+05:30";

	//console.log("string date to timestamp " + stringdate+":" + new Date(stringdate).getTime());

	return new Date(stringdate).getTime();
}

//lower limit for transaction
function getFdLowerLimit(timestamp){

	var date = new Date(timestamp + 5*60*60*1000 + 30*60*1000);
	//console.log(date);
	
	var d;
	var m;
	var y = date.getFullYear();

	if(parseInt(date.getDate()) < 10) d = "0"+date.getDate();
	else d = date.getDate(); 

	if(parseInt((date.getMonth()+1)) < 10) m = "0"+(date.getMonth()+1);
	else m = date.getMonth()+1; 

	var stringdate = y + "/" + m + "/" + d +" 09:00:00+05:30";

	//console.log("string date to timestamp " + stringdate+":" + new Date(stringdate).getTime());

	return new Date(stringdate).getTime();
}

//upper limit for transaction
function getFdUpperLimit(timestamp){

	var date = new Date(timestamp + 5*60*60*1000 + 30*60*1000);
	//console.log(date);
	
	var d;
	var m;
	var y = date.getFullYear();

	if(parseInt(date.getDate()) < 10) d = "0"+date.getDate();
	else d = date.getDate(); 

	if(parseInt((date.getMonth()+1)) < 10) m = "0"+(date.getMonth()+1);
	else m = date.getMonth()+1; 

	var stringdate = y + "/" + m + "/" + d +" 17:00:00+05:30";

	//console.log("string date to timestamp " + stringdate+":" + new Date(stringdate).getTime());

	return new Date(stringdate).getTime();
}