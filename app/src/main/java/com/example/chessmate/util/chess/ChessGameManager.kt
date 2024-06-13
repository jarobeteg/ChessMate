package com.example.chessmate.util.chess

class ChessGameManager(private val listener: ChessGameListener) {
    var moveTracker = mutableListOf<MoveTracker>()
    var chessboard: Chessboard = Chessboard()
    var chessboardEvaluator: ChessboardEvaluator = ChessboardEvaluator()
    var legalMoveGenerator: LegalMoveGenerator = LegalMoveGenerator()
    var isPlayerStarted: Boolean = true
    var isWhiteToMove: Boolean = true
    var turnNumber: Int = 1
    lateinit var player: Player
    lateinit var chessBot: ChessBot

    fun initializeGame(isPlayerStarting: Boolean, playerColor: PieceColor, botColor: PieceColor, botDepth: Int){
        initializeStartingPosition(isPlayerStarting)

        isPlayerStarted = isPlayerStarting
        chessBot = ChessBot(chessboard, chessboardEvaluator, legalMoveGenerator, botColor, botDepth)
        chessBot.isBotTurn = !isPlayerStarting
        player = Player(chessboard, chessboardEvaluator, legalMoveGenerator, playerColor)
        player.isPlayerTurn = isPlayerStarting
    }

    private fun initializeStartingPosition(isPlayerStarting: Boolean) {
        if (isPlayerStarting){//user starts as white
            chessboard.placePiece(6, 0, PieceColor.WHITE, PieceType.PAWN) //pawn in front of queen side rook
            chessboard.placePiece(6, 1, PieceColor.WHITE, PieceType.PAWN) //pawn in front of queen side knight
            chessboard.placePiece(6, 2, PieceColor.WHITE, PieceType.PAWN) //pawn in front of queen side bishop
            chessboard.placePiece(6, 3, PieceColor.WHITE, PieceType.PAWN) //pawn in front of queen
            chessboard.placePiece(6, 4, PieceColor.WHITE, PieceType.PAWN) //pawn in front of king
            chessboard.placePiece(6, 5, PieceColor.WHITE, PieceType.PAWN) //pawn in front of king side bishop
            chessboard.placePiece(6, 6, PieceColor.WHITE, PieceType.PAWN) //pawn in front of king side knight
            chessboard.placePiece(6, 7, PieceColor.WHITE, PieceType.PAWN) //pawn in front of king side rook

            chessboard.placePiece(7, 0, PieceColor.WHITE, PieceType.ROOK) //queen side rook
            chessboard.placePiece(7, 1, PieceColor.WHITE, PieceType.KNIGHT) //queen side knight
            chessboard.placePiece(7, 2, PieceColor.WHITE, PieceType.BISHOP) //queen side bishop
            chessboard.placePiece(7, 3, PieceColor.WHITE, PieceType.QUEEN) //queen
            chessboard.placePiece(7, 4, PieceColor.WHITE, PieceType.KING) //king
            chessboard.placePiece(7, 5, PieceColor.WHITE, PieceType.BISHOP) //king side bishop
            chessboard.placePiece(7, 6, PieceColor.WHITE, PieceType.KNIGHT) //king side knight
            chessboard.placePiece(7, 7, PieceColor.WHITE, PieceType.ROOK) //king side rook

            chessboard.placePiece(1, 0, PieceColor.BLACK, PieceType.PAWN) //pawn in front of queen side rook
            chessboard.placePiece(1, 1, PieceColor.BLACK, PieceType.PAWN) //pawn in front of queen side knight
            chessboard.placePiece(1, 2, PieceColor.BLACK, PieceType.PAWN) //pawn in front of queen side bishop
            chessboard.placePiece(1, 3, PieceColor.BLACK, PieceType.PAWN) //pawn in front of queen
            chessboard.placePiece(1, 4, PieceColor.BLACK, PieceType.PAWN) //pawn in front of king
            chessboard.placePiece(1, 5, PieceColor.BLACK, PieceType.PAWN) //pawn in front of king side bishop
            chessboard.placePiece(1, 6, PieceColor.BLACK, PieceType.PAWN) //pawn in front of king side knight
            chessboard.placePiece(1, 7, PieceColor.BLACK, PieceType.PAWN) //pawn in front of king side rook

            chessboard.placePiece(0, 0, PieceColor.BLACK, PieceType.ROOK) //queen side rook
            chessboard.placePiece(0, 1, PieceColor.BLACK, PieceType.KNIGHT) //queen side knight
            chessboard.placePiece(0, 2, PieceColor.BLACK, PieceType.BISHOP) //queen side bishop
            chessboard.placePiece(0, 3, PieceColor.BLACK, PieceType.QUEEN) //queen
            chessboard.placePiece(0, 4, PieceColor.BLACK, PieceType.KING) //king
            chessboard.placePiece(0, 5, PieceColor.BLACK, PieceType.BISHOP) //king side bishop
            chessboard.placePiece(0, 6, PieceColor.BLACK, PieceType.KNIGHT) //king side knight
            chessboard.placePiece(0, 7, PieceColor.BLACK, PieceType.ROOK) //king side rook
        }else{//user starts as black
            chessboard.placePiece(1, 0, PieceColor.WHITE, PieceType.PAWN) //pawn in front of king side rook
            chessboard.placePiece(1, 1, PieceColor.WHITE, PieceType.PAWN) //pawn in front of king side knight
            chessboard.placePiece(1, 2, PieceColor.WHITE, PieceType.PAWN) //pawn in front of king side bishop
            chessboard.placePiece(1, 3, PieceColor.WHITE, PieceType.PAWN) //pawn in front of king
            chessboard.placePiece(1, 4, PieceColor.WHITE, PieceType.PAWN) //pawn in front of queen
            chessboard.placePiece(1, 5, PieceColor.WHITE, PieceType.PAWN) //pawn in front of queen side bishop
            chessboard.placePiece(1, 6, PieceColor.WHITE, PieceType.PAWN) //pawn in front of queen side knight
            chessboard.placePiece(1, 7, PieceColor.WHITE, PieceType.PAWN) //pawn in front of queen side rook

            chessboard.placePiece(0, 0, PieceColor.WHITE, PieceType.ROOK) //king side rook
            chessboard.placePiece(0, 1, PieceColor.WHITE, PieceType.KNIGHT) //king side knight
            chessboard.placePiece(0, 2, PieceColor.WHITE, PieceType.BISHOP) //king side bishop
            chessboard.placePiece(0, 3, PieceColor.WHITE, PieceType.KING) //king
            chessboard.placePiece(0, 4, PieceColor.WHITE, PieceType.QUEEN) //queen
            chessboard.placePiece(0, 5, PieceColor.WHITE, PieceType.BISHOP) //queen side bishop
            chessboard.placePiece(0, 6, PieceColor.WHITE, PieceType.KNIGHT) //queen side knight
            chessboard.placePiece(0, 7, PieceColor.WHITE, PieceType.ROOK) //queen side rook

            chessboard.placePiece(6, 0, PieceColor.BLACK, PieceType.PAWN) //pawn in front of king side rook
            chessboard.placePiece(6, 1, PieceColor.BLACK, PieceType.PAWN) //pawn in front of king side knight
            chessboard.placePiece(6, 2, PieceColor.BLACK, PieceType.PAWN) //pawn in front of king side bishop
            chessboard.placePiece(6, 3, PieceColor.BLACK, PieceType.PAWN) //pawn in front of king
            chessboard.placePiece(6, 4, PieceColor.BLACK, PieceType.PAWN) //pawn in front of queen
            chessboard.placePiece(6, 5, PieceColor.BLACK, PieceType.PAWN) //pawn in front of queen side bishop
            chessboard.placePiece(6, 6, PieceColor.BLACK, PieceType.PAWN) //pawn in front of queen side knight
            chessboard.placePiece(6, 7, PieceColor.BLACK, PieceType.PAWN) //pawn in front of queen side rook

            chessboard.placePiece(7, 0, PieceColor.BLACK, PieceType.ROOK) //king side rook
            chessboard.placePiece(7, 1, PieceColor.BLACK, PieceType.KNIGHT) //king side knight
            chessboard.placePiece(7, 2, PieceColor.BLACK, PieceType.BISHOP) //king side bishop
            chessboard.placePiece(7, 3, PieceColor.BLACK, PieceType.KING) //king
            chessboard.placePiece(7, 4, PieceColor.BLACK, PieceType.QUEEN) //queen
            chessboard.placePiece(7, 5, PieceColor.BLACK, PieceType.BISHOP) //queen side bishop
            chessboard.placePiece(7, 6, PieceColor.BLACK, PieceType.KNIGHT) //queen side knight
            chessboard.placePiece(7, 7, PieceColor.BLACK, PieceType.ROOK) //queen side rook
        }
    }

    fun updateMoveTracker(move: Move){
        if (!isWhiteToMove){
            turnNumber++
        }

        val trackedMove = MoveTracker(move, turnNumber, player.playerColor, chessBot.botColor, isWhiteToMove)
        moveTracker.add(trackedMove)

        switchTurns()
    }

    fun getLastTrackedMove(): Move{
        return moveTracker.last().move
    }

    private fun switchTurns() {
        chessBot.isBotTurn = !chessBot.isBotTurn
        player.isPlayerTurn = !player.isPlayerTurn
        isWhiteToMove = !isWhiteToMove

        listener.updateMoveTrackerUI()

        if (chessBot.isBotTurn){
            chessBotTurn()
        }
    }

    private fun chessBotTurn(){
        //so far only the turn is being switched until I clean up the mess in this project
        //calling the chess bot to calculate its move needs to be in this method before switching the turn
        chessBot.getBestMove()
        switchTurns()
    }

    fun startGame(){
        if (!isPlayerStarted){
            chessBotTurn()
        }
    }

    fun handleFirstClick(square: Square){
        val legalMoves: MutableList<Move> = player.calculateLegalMoves(square)
        val kingSquare = chessboard.getKingSquare(player.playerColor)
        if (legalMoves.isEmpty() && chessboard.isKingInCheck(kingSquare, player.playerColor)){
            listener.kingIsInCheck(kingSquare)
        } else {
            listener.onPlayerMoveCalculated(legalMoves, square)
        }
    }

    fun handleSecondClick(square: Square, promotionPieceType: PieceType = PieceType.NONE){
        for (move in player.legalPlayerMoves){
            if (square.row == move.destinationPosition.row && square.col == move.destinationPosition.col) {
                if (move is PawnPromotionMove && move.promotedPieceType == promotionPieceType){
                    chessboard.performMove(move)
                    listener.onMoveMade(move)
                    break
                } else if (move is PawnPromotionCaptureMove && move.promotedPieceType == promotionPieceType){
                    chessboard.performMove(move)
                    listener.onMoveMade(move)
                    break
                } else if (promotionPieceType == PieceType.NONE){
                    chessboard.performMove(move)
                    listener.onMoveMade(move)
                    break
                }
            }
        }
    }

    fun isPromotionSquareLegal(selectedSquare: Square, promotionSquare: Square): Boolean{
        if (selectedSquare.row == 1 && promotionSquare.row == 0 && selectedSquare.pieceType == PieceType.PAWN){
            val leftCol = selectedSquare.col - 1
            val rightCol = selectedSquare.col + 1
            if ((promotionSquare.col == leftCol && chessboard.isOpponentPiece(0, leftCol, player.playerColor))
                || (promotionSquare.col == rightCol && chessboard.isOpponentPiece(0, rightCol, player.playerColor))
                || (promotionSquare.col == selectedSquare.col && !promotionSquare.isOccupied)){
                return true
            }
        }
        return false
    }

    fun squareToNotation(position: Position): String{
        val row = position.row
        val col = position.col
        val file: String
        val rank: String

        if (isPlayerStarted) {
            file = ('a' + col).toString()
            rank = (8 - row).toString()
        } else {
            file = ('h' - col).toString()
            rank = (1 + row).toString()
        }

        return "$file$rank"
    }

    fun clearPlayerMoves(){
        player.legalPlayerMoves.clear()
    }
}