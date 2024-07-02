package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.chessboard.PieceColor

class Bitboard {
    var whitePawns: Long = 0L
    var whiteKnights: Long = 0L
    var whiteBishops: Long = 0L
    var whiteRooks: Long = 0L
    var whiteQueens: Long = 0L
    var whiteKing: Long = 0L
    var blackPawns: Long = 0L
    var blackKnights: Long = 0L
    var blackBishops: Long = 0L
    var blackRooks: Long = 0L
    var blackQueens: Long = 0L
    var blackKing: Long = 0L

    companion object {
        const val A1 = 1L shl 0
        const val B1 = 1L shl 1
        const val C1 = 1L shl 2
        const val D1 = 1L shl 3
        const val E1 = 1L shl 4
        const val F1 = 1L shl 5
        const val G1 = 1L shl 6
        const val H1 = 1L shl 7
        const val A2 = 1L shl 8
        const val B2 = 1L shl 9
        const val C2 = 1L shl 10
        const val D2 = 1L shl 11
        const val E2 = 1L shl 12
        const val F2 = 1L shl 13
        const val G2 = 1L shl 14
        const val H2 = 1L shl 15
        const val A3 = 1L shl 16
        const val B3 = 1L shl 17
        const val C3 = 1L shl 18
        const val D3 = 1L shl 19
        const val E3 = 1L shl 20
        const val F3 = 1L shl 21
        const val G3 = 1L shl 22
        const val H3 = 1L shl 23
        const val A4 = 1L shl 24
        const val B4 = 1L shl 25
        const val C4 = 1L shl 26
        const val D4 = 1L shl 27
        const val E4 = 1L shl 28
        const val F4 = 1L shl 29
        const val G4 = 1L shl 30
        const val H4 = 1L shl 31
        const val A5 = 1L shl 32
        const val B5 = 1L shl 33
        const val C5 = 1L shl 34
        const val D5 = 1L shl 35
        const val E5 = 1L shl 36
        const val F5 = 1L shl 37
        const val G5 = 1L shl 38
        const val H5 = 1L shl 39
        const val A6 = 1L shl 40
        const val B6 = 1L shl 41
        const val C6 = 1L shl 42
        const val D6 = 1L shl 43
        const val E6 = 1L shl 44
        const val F6 = 1L shl 45
        const val G6 = 1L shl 46
        const val H6 = 1L shl 47
        const val A7 = 1L shl 48
        const val B7 = 1L shl 49
        const val C7 = 1L shl 50
        const val D7 = 1L shl 51
        const val E7 = 1L shl 52
        const val F7 = 1L shl 53
        const val G7 = 1L shl 54
        const val H7 = 1L shl 55
        const val A8 = 1L shl 56
        const val B8 = 1L shl 57
        const val C8 = 1L shl 58
        const val D8 = 1L shl 59
        const val E8 = 1L shl 60
        const val F8 = 1L shl 61
        const val G8 = 1L shl 62
        const val H8 = 1L shl 63
    }

    fun setupInitialBoard() {
        setPiece(BitPiece.WHITE_PAWN, A2)
        setPiece(BitPiece.WHITE_PAWN, B2)
        setPiece(BitPiece.WHITE_PAWN, C2)
        setPiece(BitPiece.WHITE_PAWN, D2)
        setPiece(BitPiece.WHITE_PAWN, E2)
        setPiece(BitPiece.WHITE_PAWN, F2)
        setPiece(BitPiece.WHITE_PAWN, G2)
        setPiece(BitPiece.WHITE_PAWN, H2)

        setPiece(BitPiece.WHITE_ROOK, A1)
        setPiece(BitPiece.WHITE_KNIGHT, B1)
        setPiece(BitPiece.WHITE_BISHOP, C1)
        setPiece(BitPiece.WHITE_QUEEN, D1)
        setPiece(BitPiece.WHITE_KING, E1)
        setPiece(BitPiece.WHITE_BISHOP, F1)
        setPiece(BitPiece.WHITE_KNIGHT, G1)
        setPiece(BitPiece.WHITE_ROOK, H1)

        setPiece(BitPiece.BLACK_PAWN, A7)
        setPiece(BitPiece.BLACK_PAWN, B7)
        setPiece(BitPiece.BLACK_PAWN, C7)
        setPiece(BitPiece.BLACK_PAWN, D7)
        setPiece(BitPiece.BLACK_PAWN, E7)
        setPiece(BitPiece.BLACK_PAWN, F7)
        setPiece(BitPiece.BLACK_PAWN, G7)
        setPiece(BitPiece.BLACK_PAWN, H7)

        setPiece(BitPiece.BLACK_ROOK, A8)
        setPiece(BitPiece.BLACK_KNIGHT, B8)
        setPiece(BitPiece.BLACK_BISHOP, C8)
        setPiece(BitPiece.BLACK_QUEEN, D8)
        setPiece(BitPiece.BLACK_KING, E8)
        setPiece(BitPiece.BLACK_BISHOP, F8)
        setPiece(BitPiece.BLACK_KNIGHT, G8)
        setPiece(BitPiece.BLACK_ROOK, H8)
    }

    private fun setPiece(piece: BitPiece, square: Long) {
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

    private fun removePiece(square: Long) {
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

    fun getPiece(square: Long): BitSquare {
        val piece = when {
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
        val color = when {
            piece.name.startsWith("WHITE") -> PieceColor.WHITE
            piece.name.startsWith("BLACK") -> PieceColor.BLACK
            else -> PieceColor.NONE
        }
        val notation = getSquareNotation(square)
        return BitSquare(square, notation, piece, color)
    }

    fun movePiece(move: BitMove) {
        if (move.capturedPiece != null) {
            removePiece(move.to)
        }
        setPiece(move.piece, move.to)
        removePiece(move.from)
    }

    fun getSquareNotation(position: Long): String {
        val fileNames = "ABCDEFGH"
        val rankNames = "12345678"

        val index = position.toULong().countTrailingZeroBits()

        val file = fileNames[index % 8]
        val rank = rankNames[index / 8]

        return "$file$rank"
    }

    fun restore(bitboard: Bitboard) {
        this.whitePawns = bitboard.whitePawns
        this.whiteKnights = bitboard.whiteKnights
        this.whiteBishops = bitboard.whiteBishops
        this.whiteRooks = bitboard.whiteRooks
        this.whiteQueens = bitboard.whiteQueens
        this.whiteKing = bitboard.whiteKing
        this.blackPawns = bitboard.blackPawns
        this.blackKnights = bitboard.blackKnights
        this.blackBishops = bitboard.blackBishops
        this.blackRooks = bitboard.blackRooks
        this.blackQueens = bitboard.blackQueens
        this.blackKing = bitboard.blackKing
    }

    fun copy(): Bitboard {
        val copy = Bitboard()
        copy.whitePawns = this.whitePawns
        copy.whiteKnights = this.whiteKnights
        copy.whiteBishops = this.whiteBishops
        copy.whiteRooks = this.whiteRooks
        copy.whiteQueens = this.whiteQueens
        copy.whiteKing = this.whiteKing
        copy.blackPawns = this.blackPawns
        copy.blackKnights = this.blackKnights
        copy.blackBishops = this.blackBishops
        copy.blackRooks = this.blackRooks
        copy.blackQueens = this.blackQueens
        copy.blackKing = this.blackKing
        return copy
    }
}