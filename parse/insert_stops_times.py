import json,httplib
import csv, sys, time

def printUsage():
	print "Usage: python insert_stops_times.py <stops_times_csv_file_path> <className>"

def buildISODateStr(timestamp):
	dateStrISO = "1993-09-30T%s.000Z" % (timestamp) #In rememberance of our great fellow Celio!
	return dateStrISO	

def buildInsertJson(stopTime):
	insertJson = {
           "method": "POST",
           "path": "/1/classes/" + className,
           "body": {
	     "stopTimeId": int(stopTime[0]), 
             "tripId": stopTime[1],
             "serviceId": stopTime[2],
	     "routeId": stopTime[3],
	     "stopId": int(stopTime[4]),
	     "arrivalTime": {
				  "__type": "Date",
				  "iso": buildISODateStr(stopTime[5])
			    },
	     "stopHeadsign": stopTime[6]
           }
         }
	return insertJson

def sendBatchJson(connection,batchJson):
	success = False
	
	while(not success):
		try:
			connection.request('POST', '/1/batch', json.dumps({"requests": batchJson}), {
			       "X-Parse-Application-Id": "QBJDdWK3jOqWFW9W4W7x940DaeO2OUxgHSPwNCos",
			       "X-Parse-REST-API-Key": "PwK0b33SFRzOfRpCJpwRmPcHXonp9JTnz7hgr4ve",
			       "Content-Type": "application/json"
			     })
			result = json.loads(connection.getresponse().read())
			#print "Result: " + str(result)

			if (result[0]['success'] != None):
				success = True
		except  Exception as e:
			print "Exception:", str(e)
		finally:
			time.sleep(2)
			

		

	
MIN_NUM_ARGS = 3
if (len(sys.argv) < MIN_NUM_ARGS):
	print "Wrong number of parameters."
	printUsage()
	exit(1)

csvFilePath = sys.argv[1]
className = sys.argv[2]

ifile  = open(csvFilePath, "rb")
reader = csv.reader(ifile)

jsonBatch = []

conn = httplib.HTTPSConnection('api.parse.com', 443)
conn.connect()

rownum = 0
for row in reader:
    # Skip header row.
	if (rownum != 0):
		#print row
		if (int(row[0])%50 == 0):
			print "Batch completed! Inserting until row#" + row[0]
			sendBatchJson(conn,jsonBatch)
			jsonBatch = []
		else:
			insertJson = buildInsertJson(row)
			jsonBatch.append(insertJson)
   	rownum += 1


#Send last rows (if any)
sendBatchJson(conn, jsonBatch)

ifile.close()

