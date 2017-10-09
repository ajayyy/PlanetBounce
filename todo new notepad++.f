it would probably best to do a for loop of 1/40 delta, and any extra save to variable to know for next time

just run update like this...

ok, thats not it, now all that is nessesary is to run this many times to simulate movement starting earlier

instead of using a time variable as player.start, use an amount of frames, maybe?


ALL DIFFERENCES FIXED, now only movement is weird... "ok, thats not it, now all that is nessesary is to run 
this many times to simulate movement starting earlier


hmm, the going left and right has an fps cap but it still has issues when combining with gravity

well, that is almost fixed, but there still seems to be slight issues, maybe it is as perfect as it gets. Try using old check
and seeing how that works.

then, finally get back to the todo list -_-

oooh, made it use frame numbers now instead. But now make it so in the client if it receives a frame number from the future
it sets it's frame number to that and simulates all things to that point (except the player in that "check")

should fix all weirdness

hmm, didn't really improve much, maybe try making the check have an actually correct method to set clientplayers coords, then 
comment it out

since the whole frame thing isnt workingproperly, the end move should supply the amount of frames it took

ok, so tons of issues were fixed (from the switch to frames, not everything was switched). Now try above. If it does not fix
move on to sublime text, its the most optimisation possible

ok, need to try the simulation with an exact number of frames.: TEST SUCCESS -- except maybe not...

could it be the bouncing?

update is not being called, instead just being called by the check method for some reason -_-

well, above is fixed, but now bouncing is still having issues, and the update from the check method is not broken anymore

try to make bouncing work the same every time (shouldn't it?)

could it be the moving left and right code (as in trying to make it move with the planet)

now it seems to be working with the test code, but not actually playing, is the server actually replaying from right frame

	- ok, works with test code confirmed, means it is now a problem of telling the server what frame the event happened at
	
	server code looks fine, see what is the issue server side
	
	ITS WHEN THE BUTTON IS HELD FOR TOO LONG (NOT WHEN IT WAS CLICKED)
	when testing, this even happens (ie both are on the same frame) could be that the code to know what angle to move is wrong

	try with only 1 planet (does it do the same thing)? --Answer is yes, meaning the angle code is CONFIRMED WRONG now. but how?
	
	weird, somehow pos gets slightly off and it must be caused by some other random thing (is the client setting its x or something)
	
	IT WAS THE FOOD ALL ALONG, fix the food I guess?
	
	Add something to a list to make the fixing happen on same thread
	
	next step is to implement the movement class into the client too for the connecting players
	
	it's not 100% perfect, but that can easily be fixed with easy checks
	

	
	clone player class for simulation, then set it
	if(player.frames < amount of frames) dont add it to the players list for a bit and set its data to its first data
		
	hmmmmm server frames are behind still?
	
proabbaly will have to move the send check message to the end of the update and make it so the client sets the pos right before starting the right frame


then make it so it will skip frames if the client got ahead --done this

(up to a limit, if it hits the limit, it will skip a few frames, move a few frames, and skip a few frames)  --not done this