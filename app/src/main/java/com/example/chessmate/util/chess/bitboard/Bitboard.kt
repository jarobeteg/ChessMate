package com.example.chessmate.util.chess.bitboard

class Bitboard {
    private var whitePawns: Long = 0L
    private var whiteKnights: Long = 0L
    private var whiteBishops: Long = 0L
    private var whiteRooks: Long = 0L
    private var whiteQueens: Long = 0L
    private var whiteKing: Long = 0L
    private var blackPawns: Long = 0L
    private var blackKnights: Long = 0L
    private var blackBishops: Long = 0L
    private var blackRooks: Long = 0L
    private var blackQueens: Long = 0L
    private var blackKing: Long = 0L

    companion object {
        val A1 = 1L shl 0
        val B1 = 1L shl 1
        val C1 = 1L shl 2
        val D1 = 1L shl 3
        val E1 = 1L shl 4
        val F1 = 1L shl 5
        val G1 = 1L shl 6
        val H1 = 1L shl 7
        val A2 = 1L shl 8
        val B2 = 1L shl 9
        val C2 = 1L shl 10
        val D2 = 1L shl 11
        val E2 = 1L shl 12
        val F2 = 1L shl 13
        val G2 = 1L shl 14
        val H2 = 1L shl 15
        val A3 = 1L shl 16
        val B3 = 1L shl 17
        val C3 = 1L shl 18
        val D3 = 1L shl 19
        val E3 = 1L shl 20
        val F3 = 1L shl 21
        val G3 = 1L shl 22
        val H3 = 1L shl 23
        val A4 = 1L shl 24
        val B4 = 1L shl 25
        val C4 = 1L shl 26
        val D4 = 1L shl 27
        val E4 = 1L shl 28
        val F4 = 1L shl 29
        val G4 = 1L shl 30
        val H4 = 1L shl 31
        val A5 = 1L shl 32
        val B5 = 1L shl 33
        val C5 = 1L shl 34
        val D5 = 1L shl 35
        val E5 = 1L shl 36
        val F5 = 1L shl 37
        val G5 = 1L shl 38
        val H5 = 1L shl 39
        val A6 = 1L shl 40
        val B6 = 1L shl 41
        val C6 = 1L shl 42
        val D6 = 1L shl 43
        val E6 = 1L shl 44
        val F6 = 1L shl 45
        val G6 = 1L shl 46
        val H6 = 1L shl 47
        val A7 = 1L shl 48
        val B7 = 1L shl 49
        val C7 = 1L shl 50
        val D7 = 1L shl 51
        val E7 = 1L shl 52
        val F7 = 1L shl 53
        val G7 = 1L shl 54
        val H7 = 1L shl 55
        val A8 = 1L shl 56
        val B8 = 1L shl 57
        val C8 = 1L shl 58
        val D8 = 1L shl 59
        val E8 = 1L shl 60
        val F8 = 1L shl 61
        val G8 = 1L shl 62
        val H8 = 1L shl 63
    }

    fun setupInitialBoard() {
        this.setPiece(BitPiece.WHITE_PAWN, A2)
        this.setPiece(BitPiece.WHITE_PAWN, B2)
        this.setPiece(BitPiece.WHITE_PAWN, C2)
        this.setPiece(BitPiece.WHITE_PAWN, D2)
        this.setPiece(BitPiece.WHITE_PAWN, E2)
        this.setPiece(BitPiece.WHITE_PAWN, F2)
        this.setPiece(BitPiece.WHITE_PAWN, G2)
        this.setPiece(BitPiece.WHITE_PAWN, H2)

        this.setPiece(BitPiece.WHITE_ROOK, A1)
        this.setPiece(BitPiece.WHITE_KNIGHT, B1)
        this.setPiece(BitPiece.WHITE_BISHOP, C1)
        this.setPiece(BitPiece.WHITE_QUEEN, D1)
        this.setPiece(BitPiece.WHITE_KING, E1)
        this.setPiece(BitPiece.WHITE_BISHOP, F1)
        this.setPiece(BitPiece.WHITE_KNIGHT, G1)
        this.setPiece(BitPiece.WHITE_ROOK, H1)

        this.setPiece(BitPiece.BLACK_PAWN, A7)
        this.setPiece(BitPiece.BLACK_PAWN, B7)
        this.setPiece(BitPiece.BLACK_PAWN, C7)
        this.setPiece(BitPiece.BLACK_PAWN, D7)
        this.setPiece(BitPiece.BLACK_PAWN, E7)
        this.setPiece(BitPiece.BLACK_PAWN, F7)
        this.setPiece(BitPiece.BLACK_PAWN, G7)
        this.setPiece(BitPiece.BLACK_PAWN, H7)

        this.setPiece(BitPiece.BLACK_ROOK, A8)
        this.setPiece(BitPiece.BLACK_KNIGHT, B8)
        this.setPiece(BitPiece.BLACK_BISHOP, C8)
        this.setPiece(BitPiece.BLACK_QUEEN, D8)
        this.setPiece(BitPiece.BLACK_KING, E8)
        this.setPiece(BitPiece.BLACK_BISHOP, F8)
        this.setPiece(BitPiece.BLACK_KNIGHT, G8)
        this.setPiece(BitPiece.BLACK_ROOK, H8)
    }

    fun setPiece(piece: BitPiece, square: Long) {
        when (piece) {
            BitPiece.WHITE_PAWN -> whitePawns = whitePawns or square
            BitPiece.WHITE_KNIGHT -> whiteKnights = whiteKnights or square
            BitPiece.WHITE_BISHOP -> whiteBishops = whiteBishops or square
            BitPiece.WHITE_ROOK -> whiteRooks = whiteRooks or square
            BitPiece.WHITE_QUEEN -> whiteQueens = whiteQueens or square
            BitPiece.WHITE_KING -> whiteKing = whiteKing or square
            BitPiece.BLACK_PAWN -> blackPawns = blackPawns or square
            BitPiece.BLACK_KNIGHT -> blackKnights = blackKnights or square
            BitPiece.BLACK_BISHOP -> blackBishops = blackBishops or square
            BitPiece.BLACK_ROOK -> blackRooks = blackRooks or square
            BitPiece.BLACK_QUEEN -> blackQueens = blackQueens or square
            BitPiece.BLACK_KING -> blackKing = blackKing or square
            BitPiece.NONE -> {}
        }
    }

    fun removePiece(square: Long) {
        whitePawns = whitePawns and square.inv()
        whiteKnights = whiteKnights and square.inv()
        whiteBishops = whiteBishops and square.inv()
        whiteRooks = whiteRooks and square.inv()
        whiteQueens = whiteQueens and square.inv()
        whiteKing = whiteKing and square.inv()
        blackPawns = blackPawns and square.inv()
        blackKnights = blackKnights and square.inv()
        blackBishops = blackBishops and square.inv()
        blackRooks = blackRooks and square.inv()
        blackQueens = blackQueens and square.inv()
        blackKing = blackKing and square.inv()
    }

    fun getPiece(square: Long): BitPiece {
        return when {
            whitePawns and square != 0L -> BitPiece.WHITE_PAWN
            whiteKnights and square != 0L -> BitPiece.WHITE_KNIGHT
            whiteBishops and square != 0L -> BitPiece.WHITE_BISHOP
            whiteRooks and square != 0L -> BitPiece.WHITE_ROOK
            whiteQueens and square != 0L -> BitPiece.WHITE_QUEEN
            whiteKing and square != 0L -> BitPiece.WHITE_KING
            blackPawns and square != 0L -> BitPiece.BLACK_PAWN
            blackKnights and square != 0L -> BitPiece.BLACK_KNIGHT
            blackBishops and square != 0L -> BitPiece.BLACK_BISHOP
            blackRooks and square != 0L -> BitPiece.BLACK_ROOK
            blackQueens and square != 0L -> BitPiece.BLACK_QUEEN
            blackKing and square != 0L -> BitPiece.BLACK_KING
            else -> BitPiece.NONE
        }
    }

    fun clear() {
        whitePawns = 0L
        whiteKnights = 0L
        whiteBishops = 0L
        whiteRooks = 0L
        whiteQueens = 0L
        whiteKing = 0L
        blackPawns = 0L
        blackKnights = 0L
        blackBishops = 0L
        blackRooks = 0L
        blackQueens = 0L
        blackKing = 0L
    }
}