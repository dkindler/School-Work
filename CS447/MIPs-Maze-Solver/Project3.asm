################################################################################################################
#
# DOCUMENTATION
#
################################################################################################################

# REGISTER $t9
# B31 to B24 	-- ROW
# B23 to B16 	-- COL
# B11 		-- If 1, facing North
# B10		-- If 1, facing right
# B9		-- If 1, facing south
# B8		-- If 1, facing left
# B3		-- If 1, facing a wall
# B2 		-- If 1, wall on left side of robot
# B1		-- If 1, wall on right side of robot
# B0		-- If 1, wall behind robot

# REGISTER $t8
# Set 1		-- Move Forward One Block
# set 2		-- Left 90 degrees
# set 3		-- Right 90 degrees
# set 4		-- do not move, update status

# $s0 will represent the moves count.
# $s1 determines whether or not we call logMoves in moveForward


################################################################################################################
#
# DOCUMENTATION
#
################################################################################################################

.data
openParen: .asciiz "("
closedParen: .asciiz ")\n"
comma: .asciiz ", "
moves:	.word 0:500
backtraceMoves: .word 0:500

.text

main:
	li $s0, 0						# ensure count is 0
	li $s1, 1						# ensure we log first move
	jal _moveForward					# move forward from (0, -1) to (0, 0)
	jal _leftHandRule					# start left hand algo
	jal _traceBack						# left hand algo finished, return back w/ best path
	jal _faceEast
	jal _moveForward
	addi	$a0, $0, 0
	addi	$a1, $0, -1
	jal _backTracking
	j gameOver
	
_leftHandRule:
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _isGameOver						# check game over
	lw $ra, 4($sp)
	addi $sp, $sp, 4

	beq $v0, 1 leftHandGameOver				# break if game over

	li $s1, 1						# make sure we log moves in _moveForward
	
	addi $sp, $sp -4
	sw $ra, 4($sp)
	jal _isWallLeft						# check if wall on left side
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	beqz $v0, leftHandNoLeftWall			
	beq $v0, 1, leftHandIsLeftWall
		
	leftHandIsLeftWall:
		addi $sp, $sp -4
		sw $ra, 4($sp)
		jal _isWallFront				# is wall in front?
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		
		beqz $v0, leftHandIsLeftWallCanMove
		beq $v0, 1, leftHandIsLeftWallCantMove
		
		leftHandIsLeftWallCanMove:
			addi $sp, $sp -4
			sw $ra, 4($sp)
			jal _moveForward			# move forward
			lw $ra, 4($sp)
			addi $sp, $sp, 4

			j _leftHandRule
		leftHandIsLeftWallCantMove:
			addi $sp, $sp -4
			sw $ra, 4($sp)
			jal _pivotRight				# pivot right
			lw $ra, 4($sp)
			addi $sp, $sp, 4
			
			j _leftHandRule
			
	leftHandNoLeftWall:
		addi $sp, $sp -4
		sw $ra, 4($sp)
		
		addi $sp, $sp -4
		sw $ra, 4($sp)
		jal _pivotLeft					# do a left turn
		jal _moveForward
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		
		j _leftHandRule
	
	leftHandGameOver:
		jr $ra

_traceBack:
	li $s1, 0						# make sure we dont log moves in _moveForward
	
	addi $sp, $sp -4
	sw $ra, 4($sp)
	jal _isAtBeggining
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	beq $v0, 1, traceBackDone				# is in (0, -1), completed.
	
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _traceBackMove
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	j _traceBack
	
	traceBackDone:
		jr $ra


########################################################
# Solves maze via backtracing recursion
# Inputs:
#	$a0 : previousRow
#	$a1 : previousCol
# Outputs:
# 	$v0 : finished bool
########################################################	
_backTracking:
	addi $sp, $sp, -12
	sw $ra, 0($sp)
	sw $a0, 4($sp)
	sw $a1, 8($sp)
	jal _getRow
	lw $a1, 8($sp)
	lw $a0, 4($sp)
	lw $ra, 0($sp)

	addi $sp, $sp, 12

	move $s4, $v0

	addi $sp, $sp, -16
	sw $ra, 0($sp)
	sw $a0, 4($sp)
	sw $a1, 8($sp)
	sw $s4, 12($sp)
	jal _getCol
	lw $s4, 12($sp)
	lw $a1, 8($sp)
	lw $a0, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 16

	move $s5, $v0

	bne $s4, 7, backTrackingNotDone
	bne $s5, 8, backTrackingNotDone

	li $v0, 1
	jr $ra

	backTrackingNotDone:
	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s4, 4($sp)
	sw $s5, 8($sp)
	sw $a0, 12($sp)
	sw $a1, 16($sp)
	jal _faceNorth
	lw $a1, 16($sp)
	lw $a0, 12($sp)
	lw $s5, 8($sp)
	lw $s4, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 20
	
	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s4, 4($sp)
	sw $s5, 8($sp)
	sw $a0, 12($sp)
	sw $a1, 16($sp)
	jal _isWallFront
	lw $a1, 16($sp)
	lw $a0, 12($sp)
	lw $s5, 8($sp)
	lw $s4, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 20
	move $t2, $v0 
	
	# Start Conditions
	beq $t2, 1, backTrackingNoNorth
	subi $t3, $s4, 1 
	beq $t3, $a0, backTrackingNoNorth 

	li $t8, 1
	subi $s4, $s4, 1
	
	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s4, 4($sp)
	sw $s5, 8($sp)
	sw $a0, 12($sp)
	sw $a1, 16($sp)
	move $a0, $t3
	move $a1, $s5
	jal _backTracking
	lw $a1, 16($sp)
	lw $a0, 12($sp)
	lw $s5, 8($sp)
	lw $s4, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 20
	beq $v0, 1, backTrackingSuccess
	addi $t8, $0, 2
	addi $t8, $0, 2
	addi $t8, $0, 1
	addi $s4, $s4, 1
	
	backTrackingNoNorth:
	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s4, 4($sp)
	sw $s5, 8($sp)
	sw $a0, 12($sp)
	sw $a1, 16($sp)
	jal _isWallRight
	lw $a1, 16($sp)
	lw $a0, 12($sp)
	lw $s5, 8($sp)
	lw $s4, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 20
	move $t2, $v0

	beq $t2, 1, backTrackingNoEast
	addi $t3, $s5, 1 
	beq $t3, $a1, backTrackingNoEast
	addi $t8, $0, 3	
	addi $t8, $0, 1
	addi $s5, $s5, 1

	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s4, 4($sp)
	sw $s5, 8($sp)
	sw $a0, 12($sp)
	sw $a1, 16($sp)
	move $a0, $s4
	move $a1, $t3
	jal _backTracking
	lw $a1, 16($sp)
	lw $a0, 12($sp)
	lw $s5, 8($sp)
	lw $s4, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 20
	beq $v0, 1, backTrackingSuccess
	addi $t8, $0, 2
	addi $t8, $0, 1
	subi $s5, $s5, 1
	
	backTrackingNoEast:
	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s4, 4($sp)
	sw $s5, 8($sp)
	sw $a0, 12($sp)
	sw $a1, 16($sp)
	jal _isWallBehind
	lw $a1, 16($sp)
	lw $a0, 12($sp)
	lw $s5, 8($sp)
	lw $s4, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 20
	move $t2, $v0

	beq $t2, 1, backTrackingNoSouth
	addi $t3, $s4, 1
	beq $t3, $a0, backTrackingNoSouth
	addi $t8, $0, 3
	addi $t8, $0, 3
	addi $t8, $0, 1
	addi $s4, $s4, 1

	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s4, 4($sp)
	sw $s5, 8($sp)
	sw $a0, 12($sp)
	sw $a1, 16($sp)
	jal _backTracking
	move $a0, $t3
	move $a1, $s5
	lw $a1, 16($sp)
	lw $a0, 12($sp)
	lw $s5, 8($sp)
	lw $s4, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 20
	beq $v0, 1, backTrackingSuccess
	addi $t8, $0, 1
	subi $s5, $s5, 1

	backTrackingNoSouth:
	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s4, 4($sp)
	sw $s5, 8($sp)
	sw $a0, 12($sp)
	sw $a1, 16($sp)
	jal _isWallLeft
	lw $a1, 16($sp)
	lw $a0, 12($sp)
	lw $s5, 8($sp)
	lw $s4, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 20
	move $t2, $v0

	beq $t2, 1, backTrackingNoWest
	addi $t3, $s5, -1	# subtract 1 from current column and store in $t3
	beq $t3, $a1, backTrackingNoWest	# if c - 1 == pc, go to conditionNotMet
	addi $t8, $0, 2	# turn car to face west
	addi $t8, $0, 1	# advance car
	subi $s5, $s5, 1


	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s4, 4($sp)
	sw $s5, 8($sp)
	sw $a0, 12($sp)
	sw $a1, 16($sp)
	jal _backTracking
	move $a0, $s4
	move $a1, $t3
	lw $a1, 16($sp)
	lw $a0, 12($sp)
	lw $s5, 8($sp)
	lw $s4, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 20
	beq $v0, 1, backTrackingSuccess
	addi $t8, $0, 3	# go back
	addi $t8, $0, 1
	addi $s5, $s5, 1
	
	backTrackingNoWest:
	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s4, 4($sp)
	sw $s5, 8($sp)
	sw $a0, 12($sp)
	sw $a1, 16($sp)
	jal _faceNorth
	lw $a1, 16($sp)
	lw $a0, 12($sp)
	lw $s5, 8($sp)
	lw $s4, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 20
	
	li $v0, 0
	jr $ra
	
	backTrackingSuccess:
	li	$v0, 1
	jr	$ra
	

################################################################################################################
#
# THE FOLLOWING BELOW ARE THE HANDLERS FOR THE MOVEMENT OF THE CAR
#
################################################################################################################

########################################################
# Trace Back Move
# Performs a move heading towards (0, -1)
########################################################
_traceBackMove:
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _popMove
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	add $s3, $zero, $v0					# $s3 is now the current move we must trace
	
	#### FACING NORTH
	add $v0, $zero, $s3	# Set value of $v0 to be $t9
	srl $v0, $v0, 11	# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	beq $v0, 1, traceBackMoveGoSouth
	
	#### FACING EAST
	add $v0, $zero, $s3	# Set value of $v0 to be $t9
	srl $v0, $v0, 10	# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	beq $v0, 1, traceBackMoveGoWest
	
	#### FACING SOUTH
	add $v0, $zero, $s3	# Set value of $v0 to be $t9
	srl $v0, $v0, 9		# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	beq $v0, 1, traceBackMoveGoNorth
	
	#### FACING WEST
	add $v0, $zero, $s3	# Set value of $v0 to be $t9
	srl $v0, $v0, 8		# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	beq $v0, 1, traceBackMoveGoEast
	
	traceBackMoveGoSouth:
		addi $sp, $sp -4
		sw $ra, 4($sp) 
		jal _faceSouth
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		
		beq  $v0, 1, traceBackMoveExecute
		
	traceBackMoveGoNorth:
		addi $sp, $sp -4
		sw $ra, 4($sp) 
		jal _faceNorth
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		
		beq  $v0, 1, traceBackMoveExecute
			
	traceBackMoveGoEast:
		addi $sp, $sp -4
		sw $ra, 4($sp) 
		jal _faceEast
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		
		beq  $v0, 1, traceBackMoveExecute

	traceBackMoveGoWest:
		addi $sp, $sp -4
		sw $ra, 4($sp) 
		jal _faceWest
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		
		beq  $v0, 1, traceBackMoveExecute
		
	traceBackMoveExecute:
		addi $sp, $sp -4
		sw $ra, 4($sp) 
		jal _moveForward
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		
		jr $ra	

_updateStatus:
	li $t8, 4
	updateStatusLoop:
		bnez $t8, updateStatusLoop
		addi $sp, $sp -4
		sw $ra, 4($sp)
		jal _printCell
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		jr $ra

_moveForward:
	li $t8, 1
	
	moveForwardLoop:
		bnez $t8, moveForwardLoop
		
		addi $sp, $sp -4
		sw $ra, 4($sp)
		jal _updateStatus
		lw $ra, 4($sp)
		addi $sp, $sp, 4

		beqz $s1, moveForwardLoopBreak			# if $s0 set to 0 then don't log move
	
		addi $sp, $sp -4
		sw $ra, 4($sp)
		jal _logMove
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		
		moveForwardLoopBreak:
			jr $ra

_pivotRight:
	li $t8, 3
	turnRightLoop:
		bnez $t8, turnRightLoop
		addi $sp, $sp -4
		sw $ra, 4($sp)
		jal _updateStatus
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		jr $ra

_pivotLeft:
	li $t8, 2
	turnLeftLoop:
		bnez $t8, turnLeftLoop
		addi $sp, $sp -4
		sw $ra, 4($sp) 
		jal _updateStatus
		lw $ra, 4($sp)
		addi $sp, $sp, 4
		jr $ra

_faceNorth:
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _isNorth
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	beq, $v0, 1, faceNorthBreak
	
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _pivotRight
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	j _faceNorth
	
	faceNorthBreak:
		jr $ra
	
_faceSouth:
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _isSouth
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	beq, $v0, 1, faceSouthBreak
	
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _pivotLeft
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	j _faceSouth
	
	faceSouthBreak:
		jr $ra

_faceEast:
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _isEast
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	beq, $v0, 1, faceEastBreak
	
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _pivotRight
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	j _faceEast
	
	faceEastBreak:
		jr $ra
_faceWest:
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _isWest
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	beq, $v0, 1, faceWestBreak
	
	addi $sp, $sp -4
	sw $ra, 4($sp) 
	jal _pivotLeft
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	j _faceWest
	
	faceWestBreak:
		jr $ra
		

################################################################################################################
#
# HELPERS
#
################################################################################################################

########################################################
# Print Out cell in (Row, Col)
########################################################
_printCell:
	#print open parens
	la $a0, openParen
	li $v0, 4
	syscall
	
	addi $sp, $sp -4
	sw $ra, 4($sp)
	jal _getRow
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	#Print Row
	add $a0, $zero, $v0
	li $v0, 1
	syscall
	
	#print open comma
	la $a0, comma
	li $v0, 4
	syscall
	
	addi $sp, $sp -4
	sw $ra, 4($sp)
	jal _getCol
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	#Print Col
	add $a0, $zero, $v0
	li $v0, 1
	syscall
	
	#print open parens
	la $a0, closedParen
	li $v0, 4
	syscall
	
	jr $ra

########################################################
# Pops a move from moves array
# Outputs:
#	$v0 : move
########################################################
_popMove:
	beqz $s0, popMoveEnd
	
	subi $t0, $s0, 1
	li $t1, 4
	mult $t0, $t1
	mflo $t0
	
	lw $v0, moves($t0)
	sw $zero, moves($t0)
	subi $s0, $s0, 1
	
	popMoveEnd:
		jr $ra

########################################################
# Logs Moves For Direct Path
########################################################
_logMove:
	li $t0, 0
	beqz $s0, logMoveAddMove

	subi $t5, $s0, 2				# setting $t5 to count - 2... 1 for indexing, 1 to check 2nd to last

	li $t0, 4
	mult $t5, $t0
	mflo $t0

	lw $t1, moves($t0)
	
	addi $sp, $sp -4
	sw $ra, 4($sp)
	jal _cellEqualT9
	lw $ra, 4($sp)
	addi $sp, $sp, 4
	
	bnez $v0, logMoveDeleteMove
	beqz $v0, logMoveAddMove

	logMoveDeleteMove:
		subi $t5, $s0, 1
		li $t0, 4
		mult $t5, $t0
		mflo $t0

		sw $zero, moves($t0)
		subi $s0, $s0, 1
		jr $ra
		
	logMoveAddMove:
		li $t0, 4
		mult $s0, $t0
		mflo $t0
		sw $t9, moves($t0)
		addi $s0, $s0, 1
		jr $ra

########################################################
# Checks if a given Cell is equal to the current cell
# Inputs:
# 	$t1: number 1
# Outputs:
#	$v0 : bool
########################################################

_cellEqualT9:
	add $v0, $zero, $t9	#col $t9
	srl $v0, $v0, 16	
	andi $v0, $v0, 255
	
	add $v1, $zero, $t1 	#col $t1
	srl $v1, $v1, 16
	andi $v1, $v1, 255	
	
	bne $v0, $v1, cellEqualFalse
	
	add $v0, $zero, $t9	# row $t9
	srl $v0, $v0, 24
	add $v1, $zero, $t1	# row $t1
	srl $v1, $v1, 24
	
	bne $v0, $v1, cellEqualFalse
	beq $v0, $v1, cellEqualTrue
	
	cellEqualTrue:
		li $v0, 1
		jr $ra
	cellEqualFalse:
		li $v0, 0
		jr $ra
		

########################################################
# Returns 1 if car is in cell (0, -1) 
# Outputs:
#	$v0 : bool
########################################################
_isAtBeggining:
	addi $sp, $sp -4
	sw $ra, 4($sp)
	jal _getRow
	add $t0, $zero, $v0
	jal _getCol
	add $t1, $zero, $v0
	lw $ra, 4($sp)
	addi $sp, $sp, 4

	bne $t0, 0, isAtBegginingNo
	bne $t1, 255, isAtBegginingNo
	
	li $v0, 1
	jr $ra
	
	isAtBegginingNo:
		li $v0, 0
		jr $ra

			
########################################################
# Returns 1 if game is over
# Outputs:
#	$v0 : Bool
########################################################
_isGameOver:
	addi $sp, $sp -4
	sw $ra, 4($sp)
	jal _getRow
	add $t0, $zero, $v0
	jal _getCol
	add $t1, $zero, $v0
	lw $ra, 4($sp)
	addi $sp, $sp, 4	

	bne $t0, 7, gameOverNo
	bne $t1, 8, gameOverNo
	
	li $v0, 1
	jr $ra
	
	gameOverNo:
		li $v0, 0
		jr $ra


################################################################################################################
#
# THE FOLLOWING CODE BELOW ARE THE HANDLERS FOR THE $t9 REGISTER
#
################################################################################################################

########################################################
# Returns the Car's current row
# Outputs:
#	$v0 : row
########################################################
_getRow:
	add $v0, $zero, $t9	# Set value of $v0 to be $t9
	srl $v0, $v0, 24	# shift right 24 bits
	jr $ra
	
########################################################
# Returns the Car's current col
# Outputs:
#	$v0 : col
########################################################
_getCol:
	add $v0, $zero, $t9	# Set value of $v0 to be $t9
	srl $v0, $v0, 16	# shift right logical 16bits
	andi $v0, $v0, 255
	jr $ra
	
########################################################
# Returns 1 if car is facing north
# Outputs:
#	$v0 : isNorth
########################################################
_isNorth:
	add $v0, $zero, $t9	# Set value of $v0 to be $t9
	srl $v0, $v0, 11	# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	jr $ra
	
########################################################
# Returns 1 if car is facing south
# Outputs:
#	$v0 : isSouth
########################################################
_isSouth:
	add $v0, $zero, $t9	# Set value of $v0 to be $t9
	srl $v0, $v0, 9		# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	jr $ra
	
########################################################
# Returns 1 if car is facing east
# Outputs:
#	$v0 : isEast
########################################################
_isEast:
	add $v0, $zero, $t9	# Set value of $v0 to be $t9
	srl $v0, $v0, 10	# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	jr $ra
	
########################################################
# Returns 1 if car is facing west
# Outputs:
#	$v0 : isWest
########################################################
_isWest:
	add $v0, $zero, $t9	# Set value of $v0 to be $t9
	srl $v0, $v0, 8		# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	jr $ra
	
########################################################
# Returns 1 if wall in front of car
# Outputs:
#	$v0 : isWall
########################################################
_isWallFront:
	add $v0, $zero, $t9	# Set value of $v0 to be $t9
	srl $v0, $v0, 3		# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	jr $ra
	
########################################################
# Returns 1 if wall behind of car
# Outputs:
#	$v0 : isWall
########################################################
_isWallBehind:
	add $v0, $zero, $t9	# Set value of $v0 to be $t9
	andi $v0, $v0, 1	# get value of last bit
	jr $ra
	
########################################################
# Returns 1 if wall on right of robot
# Outputs:
#	$v0 : isWall
########################################################
_isWallRight:
	add $v0, $zero, $t9	# Set value of $v0 to be $t9
	srl $v0, $v0, 1		# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	jr $ra
	
########################################################
# Returns 1 if wall on left of robot
# Outputs:
#	$v0 : isWall
########################################################
_isWallLeft:
	add $v0, $zero, $t9	# Set value of $v0 to be $t9
	srl $v0, $v0, 2		# shift right 11 bits
	andi $v0, $v0, 1	# get value of last bit
	jr $ra



#########################################################
gameOver:
	li $v0, 10
	syscall
