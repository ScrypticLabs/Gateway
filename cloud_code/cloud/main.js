// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:


function initChatWithAI(user, AI, response) {
	var Groups = Parse.Object.extend("Groups");
	var gatewayObjectID = AI.id;
	var groups = new Groups();
	groups.set("Name", "AI");
	groups.set("UserPointers", [
		{"__type":"Pointer","className”:”_User”,”objectId":gatewayObjectID},
		{"__type":"Pointer","className”:”_User”,”objectId":user.id}]);
	groups.set("Users", gatewayObjectID+","+user.id);

	groups.save(null, {
  		success: function(groups) {	// param groups represents the groups object that is being added to Groups
    		// Execute any logic that should take place after the object is saved.
    		addChatToAI(user, AI, groups);
			addChatToUser(user, AI, groups, response);
  		},
  		error: function(groups, error) {
    		// Execute any logic that should take place if the save fails.
    		// error is a Parse.Error with an error code and message.
    		alert('Failed to create new object, with error code: ' + error.message);
  		}
	});
}

function addChatToAI(user, ai, groups) {
	var AI = Parse.Object.extend("Gateway");
	var gatewayObjectID = ai.id;
	var gateway = new AI();

	gateway.set("conversationName", "AI");
	gateway.set("messageFrom", {"__type":"Pointer","className":"_User","objectId":""+ gatewayObjectID + ""});
	gateway.set("messageTo", {"__type":"Pointer","className":"Groups","objectId":""+ groups.id + ""});
	gateway.set("Users", gatewayObjectID+","+user.id);

	gateway.save(null, {
  		success: function(gateway) {
    		// Execute any logic that should take place after the object is saved.
  		},
  		error: function(gateway, error) {
    		// Execute any logic that should take place if the save fails.
    		// error is a Parse.Error with an error code and message.
    		alert('Failed to create new object, with error code: ' + error.message);
    		 // console.log("Failed to add chat to AI");
  		}
	});
}

function addChatToUser(user, AI, groups, response) {
	var Profile = Parse.Object.extend("User_"+user.id);
	var profile = new Profile();
	var gatewayObjectID = AI.id;

	alert(groups.get("Users"));

	profile.set("conversationName", "AI");
	profile.set("messageFrom", {"__type":"Pointer","className":"_User","objectId":""+ user.id + ""});
	profile.set("messageTo", {"__type":"Pointer","className":"Groups","objectId":""+ groups.id + ""});
	profile.set("Users", gatewayObjectID+","+user.id);

	profile.save(null, {
  		success: function(group) {
    		// Execute any logic that should take place after the object is saved.
    		// message = "Successfully added chat to AI";
    		sendMessageOnSignUp(user, AI, groups, "Hi, I'm Gateway! You can ask me questions or tell me to do some tasks for you.", response);
  		},
  		error: function(group, error) {
    		// Execute any logic that should take place if the save fails.
    		// error is a Parse.Error with an error code and message.
    		alert('Failed to create new object, with error code: ' + error.message);
    		 // console.log("Failed to add chat to AI");
    		// message = error.message;
  		}
	});
}

function sendMessageOnSignUp(user, AI, to, message, response) {
	// from is a user
	// to is a group
	var User = Parse.Object.extend("User_"+user.id);
	var conversations = new Parse.Query(User);
	conversations.equalTo("Users", to.get("Users"));
	
	// finding a conversation that is pointing to 'to' verifies its existence
	conversations.find({
  		success: function(results) {
   			// alert(to.get("Users")+"  Successfully retrieved " + results.length + " scores.");
    		// Do something with the returned Parse.Object values
    		
    		var Conversation = Parse.Object.extend("Convo_"+to.id);	// to.id can be used because since the conversation is found, 'to' actually is the correct recipient
    		var conversation = new Conversation();

    		// alert("IDS: "+to.id+"  "+results[0].get("messageTo"));

    		conversation.set("messageFrom", {"__type":"Pointer","className":"_User","objectId":""+ AI.id + ""});
    		conversation.set("messageTo", {"__type":"Pointer","className":"Groups","objectId":""+to.id+""});
    		conversation.set("message", message);

    		conversation.save(null, {
    			success: function(conversation) {
    				alert("Conversation created with id "+conversation.id);
    				response.success("User signed up successfully!");
    			},
    			error: function(conversation, error) {
    				alert("Conversation not created with error: "+error.message);
    				response.error("User could not be signed up!");
    			} 
    		});

  		},
  		error: function(error) {
    		alert("Error: " + error.code + " " + error.message);
  		}
	});
}

function addMessageToConvo(message, client, clientFrom, clientTo, response) {
	var Conversation = Parse.Object.extend("Convo_"+clientTo.id);	// to.id can be used because since the conversation is found, 'to' actually is the correct recipient
	var conversation = new Conversation();

	conversation.set("messageFrom", {"__type":"Pointer","className":"_User","objectId":""+ clientFrom.id + ""});
	conversation.set("messageTo", {"__type":"Pointer","className":"Groups","objectId":""+clientTo.id+""});
	conversation.set("message", message);

	conversation.save(null, {
		success: function(conversation) {
			alert("Message added with id "+conversation.id);
			createQueueOfMessages(message, client, clientFrom, clientTo, response);
		},
		error: function(conversation, error) {
			alert("Conversation not created with error: "+error.message);
		} 
	});
} 

function createQueueOfMessages(message, client, clientFrom, group, response) {
	var Queue = Parse.Object.extend("Queue_"+client);
	var queue = new Queue();

	var convoID = "Convo_"+group.id+"";

	queue.set("message", message);
	queue.set("convoID", convoID);
	queue.set("messageFrom", {"__type":"Pointer","className":"_User","objectId":""+ clientFrom.id + ""});
	queue.set("messageFromString", ""+clientFrom.id+"");
	queue.set("group", {"__type":"Pointer","className":"Groups","objectId":""+ group.id + ""});

	queue.save(null, {
  		success: function(queue) {
    		// Execute any logic that should take place after the object is saved.
    		response.success(clientFrom.get("firstName")+" successfully sent a message to "+client);
  		},
  		error: function(queue, error) {
    		// Execute any logic that should take place if the save fails.
    		// error is a Parse.Error with an error code and message.
    		alert('Failed to create queue, with error code: ' + error.message);
    		 // console.log("Failed to add chat to AI");
    		response.error(clientFrom.get("firstName")+" failed to send a message to "+client);

  		}
	});
}


Parse.Cloud.define("signUp", function(request, response) {
	var firstName = request.params.firstName;
	var lastName = request.params.lastName;
	var email = request.params.email;
	var password = request.params.password;

	var query = new Parse.Query(Parse.User);
	query.equalTo("objectId", "YFCwmgsRZR");  // get gateway user object
	query.find({
  		success: function(query) {
  			var user = new Parse.User();
			user.set("username", email);
			user.set("email", email);
			user.set("password", password);
			user.set("firstName", firstName);
			user.set("lastName", lastName);

			// To check if the User Gateway was found - currently it works
			// for (var i = query.length - 1; i >= 0; i--) {
			// 	alert(query[i].get("firstName"));
			// }

			user.signUp(null, {
		  		success: function(user) {
		    		// Hooray! Let them use the app now.
		    		initChatWithAI(user, query[0], response);
		    		// response.success("Success: "+firstName+" "+lastName+" was signed up!");
		  		},
		  		error: function(user, error) {
		    		// Show the error message somewhere and let the user try again.
		    		response.error("Error: " + error.code + " " + error.message);
		  		}
			});
  		}
	});
});

Parse.Cloud.define("sendMessage", function(request, response) {
	var clientFrom = request.params.from;		// clientFrom is a user's object id in string
	var clientTo = request.params.to;			// clientTo is a group's object id
	var message = request.params.message;

	var fromUser = new Parse.Query(Parse.User);
	fromUser.equalTo("objectId", clientFrom);

	fromUser.find({
		success: function(fromUser) {
			// alert("User who sent this message is: "+fromUser[0].get("firstName"));
			var Group = Parse.Object.extend("Groups");
			var toGroup = new Parse.Query(Group);
			toGroup.equalTo("objectId", clientTo);

			toGroup.find({
				success: function(toGroup) {
					var clients = toGroup[0].get("Users").split(",");
					for (var index in clients) {
						var client = clients[index];
						if (client.localeCompare(""+fromUser[0].id+"") != 0) {
							addMessageToConvo(message, client, fromUser[0], toGroup[0], response);
						}
					}
				}
			});
		}
	});
});

Parse.Cloud.define("hello", function(request, response) {
  response.success("From Gateway to You: "+request.params);
});