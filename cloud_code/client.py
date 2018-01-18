#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: Abhi Gupta
# @Date:   2015-09-12 15:15:27
# @Last Modified by:   Abhi
# @Last Modified time: 2016-06-12 20:27:13

import json,httplib
connection = httplib.HTTPSConnection('api.parse.com', 443)
connection.connect()
connection.request('POST', '/1/functions/signUp', json.dumps({
	"firstName" : "Shashi",
	"lastName" : "Gupta", 
	"password" : "hey", 
	"email" : "soccermomshashi@gmail.com"
	}), {
       "X-Parse-Application-Id": "JtoODYD7UOMEo5e0h8aDynD4gD4fxhjopsuPc8EQ",
       "X-Parse-REST-API-Key": "tD8qd9PB3DnanHuq38SD6worauurx9sQIRy1CHHn",
       "Content-Type": "application/json"
     })
result = json.loads(connection.getresponse().read())
print result

