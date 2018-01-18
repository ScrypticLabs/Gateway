# -*- coding: utf-8 -*-
# @Author: Abhi
# @Date:   2016-06-13 12:26:53
# @Last Modified by:   Abhi
# @Last Modified time: 2016-06-16 19:21:45

import json,httplib
connection = httplib.HTTPSConnection('api.parse.com', 443)
connection.connect()
connection.request('POST', '/1/functions/sendMessage', json.dumps({
	"from" : "A4XJe9QCQ6",
	"to" : "9tFqD8BzHg", 
	"message" : "SUUUp"
	}), {
       "X-Parse-Application-Id": "JtoODYD7UOMEo5e0h8aDynD4gD4fxhjopsuPc8EQ",
       "X-Parse-REST-API-Key": "tD8qd9PB3DnanHuq38SD6worauurx9sQIRy1CHHn",
       "Content-Type": "application/json"
     })
result = json.loads(connection.getresponse().read())
print result

# connection.request
 