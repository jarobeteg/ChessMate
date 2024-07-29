package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.chessboard.PieceColor

class Bitboard {
    var bitboards = LongArray(12)
    var castlingRights: Int = 0xF

    private val castlingRightsMap = mapOf(
        BitPiece.WHITE_KING to listOf(WHITE_KINGSIDE, WHITE_QUEENSIDE),
        BitPiece.BLACK_KING to listOf(BLACK_KINGSIDE, BLACK_QUEENSIDE),
        BitPiece.WHITE_ROOK to mapOf(BitCell.A1.bit to WHITE_QUEENSIDE, BitCell.H1.bit to WHITE_KINGSIDE),
        BitPiece.BLACK_ROOK to mapOf(BitCell.A8.bit to BLACK_QUEENSIDE, BitCell.H8.bit to BLACK_KINGSIDE)
    )

    companion object {
        const val RANK_1 = 0xFFL
        const val RANK_2 = RANK_1 shl 8
        const val RANK_3 = RANK_1 shl 16
        const val RANK_4 = RANK_1 shl 24
        const val RANK_5 = RANK_1 shl 32
        const val RANK_6 = RANK_1 shl 40
        const val RANK_7 = RANK_1 shl 48
        const val RANK_8 = RANK_1 shl 56

        const val FILE_A = 0x101010101010101L
        const val FILE_B = FILE_A shl 1
        const val FILE_C = FILE_A shl 2
        const val FILE_D = FILE_A shl 3
        const val FILE_E = FILE_A shl 4
        const val FILE_F = FILE_A shl 5
        const val FILE_G = FILE_A shl 6
        const val FILE_H = FILE_A shl 7

        const val WHITE_KINGSIDE = 0x1
        const val WHITE_QUEENSIDE = 0x2
        const val BLACK_KINGSIDE = 0x4
        const val BLACK_QUEENSIDE = 0x8
    }

    fun setupInitialBoard() {
        setPiece(BitPiece.WHITE_PAWN, RANK_2)
        setPiece(BitPiece.WHITE_ROOK, RANK_1 and (FILE_A or FILE_H))
        setPiece(BitPiece.WHITE_KNIGHT, RANK_1 and (FILE_B or FILE_G))
        setPiece(BitPiece.WHITE_BISHOP, RANK_1 and (FILE_C or FILE_F))
        setPiece(BitPiece.WHITE_QUEEN, RANK_1 and FILE_D)
        setPiece(BitPiece.WHITE_KING, RANK_1 and FILE_E)

        setPiece(BitPiece.BLACK_PAWN, RANK_7)
        setPiece(BitPiece.BLACK_ROOK, RANK_8 and (FILE_A or FILE_H))
        setPiece(BitPiece.BLACK_KNIGHT, RANK_8 and (FILE_B or FILE_G))
        setPiece(BitPiece.BLACK_BISHOP, RANK_8 and (FILE_C or FILE_F))
        setPiece(BitPiece.BLACK_QUEEN, RANK_8 and FILE_D)
        setPiece(BitPiece.BLACK_KING, RANK_8 and FILE_E)
    }

    private fun setPiece(piece: BitPiece, square: Long) {
        val index = piece.ordinal
        bitboards[index] = bitboards[index] or square
    }

    private fun removePiece(piece: BitPiece, square: Long) {
        if (piece != BitPiece.NONE) {
            val index = piece.ordinal
            bitboards[index] = bitboards[index] and square.inv()
        }
    }

    fun getPiece(square: Long): BitSquare {
        for ((index, bitboard) in bitboards.withIndex()) {
            if (bitboard and square != 0L) {
                val piece = BitPiece.entries[index]
                val color = if (index < 6) PieceColor.WHITE else PieceColor.BLACK
                return BitSquare(square, getSquareNotation(square), piece, color)
            }
        }
        return BitSquare(square, getSquareNotation(square), BitPiece.NONE, PieceColor.NONE)
    }

    fun movePiece(move: BitMove) {
        castlingRightsMap[move.piece].let { rights ->
            when (rights) {
                is List<*> -> rights.forEach { revokeCastlingRights(it as Int) }
                is Map<*, *> -> {
                    val right = rights[move.from] as? Int
                    if (right != null) {
                        revokeCastlingRights(right)
                    }
                }
            }
        }

        if (move.isCastling) {
            handleCastlingMove(move)
        } else if (move.promotion != BitPiece.NONE) {
            removePiece(move.capturedPiece, move.to)
            setPiece(move.promotion, move.to)
            removePiece(move.piece, move.from)
        } else if (move.isEnPassant) {
            handleEnPassantMove(move)
        } else {
            if (move.capturedPiece == BitPiece.WHITE_ROOK) {
                if (move.to == BitCell.A1.bit) revokeCastlingRights(WHITE_QUEENSIDE)
                if (move.to == BitCell.H1.bit) revokeCastlingRights(WHITE_KINGSIDE)
            }
            if (move.capturedPiece == BitPiece.BLACK_ROOK) {
                if (move.to == BitCell.A8.bit) revokeCastlingRights(BLACK_QUEENSIDE)
                if (move.to == BitCell.H8.bit) revokeCastlingRights(BLACK_KINGSIDE)
            }
            removePiece(move.capturedPiece, move.to)
            setPiece(move.piece, move.to)
            removePiece(move.piece, move.from)
        }
    }

    private fun handleEnPassantMove(move: BitMove) {

    }

    private fun handleCastlingMove(move: BitMove) {
        if (move.piece == BitPiece.WHITE_KING) {
            if (move.to == BitCell.G1.bit) { //white kingside castles
                setPiece(BitPiece.WHITE_KING, BitCell.G1.bit)
                removePiece(BitPiece.WHITE_KING, BitCell.E1.bit)

                setPiece(BitPiece.WHITE_ROOK, BitCell.F1.bit)
                removePiece(BitPiece.WHITE_ROOK, BitCell.H1.bit)
            } else if (move.to == BitCell.C1.bit) { //white queenside castles
                setPiece(BitPiece.WHITE_KING, BitCell.C1.bit)
                removePiece(BitPiece.WHITE_KING, BitCell.E1.bit)

                setPiece(BitPiece.WHITE_ROOK, BitCell.D1.bit)
                removePiece(BitPiece.WHITE_ROOK, BitCell.A1.bit)
            }
        } else if (move.piece == BitPiece.BLACK_KING) {
            if (move.to == BitCell.G8.bit) { //black kingside castles
                setPiece(BitPiece.BLACK_KING, BitCell.G8.bit)
                removePiece(BitPiece.BLACK_KING, BitCell.E8.bit)

                setPiece(BitPiece.BLACK_ROOK, BitCell.F8.bit)
                removePiece(BitPiece.BLACK_ROOK, BitCell.H8.bit)
            } else if (move.to == BitCell.C8.bit) { //black queenside castles
                setPiece(BitPiece.BLACK_KING, BitCell.C8.bit)
                removePiece(BitPiece.BLACK_KING, BitCell.E8.bit)

                setPiece(BitPiece.BLACK_ROOK, BitCell.D8.bit)
                removePiece(BitPiece.BLACK_ROOK, BitCell.A8.bit)
            }
        }
    }

    private fun revokeCastlingRights(rights: Int) {
        castlingRights = castlingRights and rights.inv()
    }

    fun grantCastlingRights(rights: Int) {
        castlingRights = castlingRights or rights
    }

    fun hasCastlingRights(rights: Int): Boolean {
        return (castlingRights and rights) != 0
    }

    fun isSquareEmpty(square: Long): Boolean {
        return (getAllPieces() and square) == 0L
    }

    private fun getSquareNotation(position: Long): String {
        val fileNames = "ABCDEFGH"
        val rankNames = "12345678"

        val index = position.toULong().countTrailingZeroBits()

        val file = fileNames[index % 8]
        val rank = rankNames[index / 8]

        return "$file$rank"
    }

    fun getWhitePieceBitboards(): LongArray {
        return longArrayOf(
            bitboards[BitPiece.WHITE_PAWN.ordinal],
            bitboards[BitPiece.WHITE_KNIGHT.ordinal],
            bitboards[BitPiece.WHITE_BISHOP.ordinal],
            bitboards[BitPiece.WHITE_ROOK.ordinal],
            bitboards[BitPiece.WHITE_QUEEN.ordinal],
            bitboards[BitPiece.WHITE_KING.ordinal]
        )
    }

    fun getBlackPieceBitboards(): LongArray {
        return longArrayOf(
            bitboards[BitPiece.BLACK_PAWN.ordinal],
            bitboards[BitPiece.BLACK_KNIGHT.ordinal],
            bitboards[BitPiece.BLACK_BISHOP.ordinal],
            bitboards[BitPiece.BLACK_ROOK.ordinal],
            bitboards[BitPiece.BLACK_QUEEN.ordinal],
            bitboards[BitPiece.BLACK_KING.ordinal]
        )
    }

    fun getAllWhitePieces(): Long {
        return bitboards[BitPiece.WHITE_PAWN.ordinal] or
                bitboards[BitPiece.WHITE_KNIGHT.ordinal] or
                bitboards[BitPiece.WHITE_BISHOP.ordinal] or
                bitboards[BitPiece.WHITE_ROOK.ordinal] or
                bitboards[BitPiece.WHITE_QUEEN.ordinal] or
                bitboards[BitPiece.WHITE_KING.ordinal]
    }

    fun getAllBlackPieces(): Long {
        return bitboards[BitPiece.BLACK_PAWN.ordinal] or
                bitboards[BitPiece.BLACK_KNIGHT.ordinal] or
                bitboards[BitPiece.BLACK_BISHOP.ordinal] or
                bitboards[BitPiece.BLACK_ROOK.ordinal] or
                bitboards[BitPiece.BLACK_QUEEN.ordinal] or
                bitboards[BitPiece.BLACK_KING.ordinal]
    }

    fun getAllPieces(): Long {
        return getAllWhitePieces() or getAllBlackPieces()
    }

    fun getWhiteKing(): Long {
        return bitboards[BitPiece.WHITE_KING.ordinal]
    }

    fun getBlackKing(): Long {
        return bitboards[BitPiece.BLACK_KING.ordinal]
    }

    fun restore(bitboard: Bitboard) {
        this.bitboards = bitboard.bitboards.clone()
        this.castlingRights = bitboard.castlingRights
    }

    fun copy(): Bitboard {
        val copy = Bitboard()
        copy.bitboards = this.bitboards.clone()
        copy.castlingRights = this.castlingRights
        return copy
    }

}