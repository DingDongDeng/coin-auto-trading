package com.dingdongdeng.autotrading.domain.indicator.factory

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.domain.indicator.model.BollingerBands
import com.dingdongdeng.autotrading.domain.indicator.model.Indicators
import com.dingdongdeng.autotrading.domain.indicator.model.Ma
import com.dingdongdeng.autotrading.domain.indicator.model.Macd
import com.dingdongdeng.autotrading.domain.indicator.model.Obv
import com.tictactec.ta.lib.Core
import com.tictactec.ta.lib.MAType
import com.tictactec.ta.lib.MInteger

object IndicatorFactory {
    private val core = Core()

    fun calculate(candles: List<ExchangeChartCandle>): Indicators {
        return Indicators(
            indicatorDateTimeKst = candles.last().candleDateTimeKst,
            rsiFunc = { getRsi(candles) },
            macdFunc = { getMACD(candles) },
            bollingerBandsFunc = { getBollingerBands(candles) },
            obvFunc = { getObv(candles) },
            maFunc = { getMv(candles) },
        )
    }

    fun getMv(candles: List<ExchangeChartCandle>): Ma {
        val inReal = candles.map { it.closingPrice }.toDoubleArray()
        return Ma(
            sma10Func = {
                // SMA 10
                val SMA10_TIME_PERIOD = 10
                val sma10OutBegIdx = MInteger()
                val sma10OutNBElement = MInteger()
                val sma10OutReal = DoubleArray(inReal.size)
                core.sma(0, inReal.size - 1, inReal, SMA10_TIME_PERIOD, sma10OutBegIdx, sma10OutNBElement, sma10OutReal)
                sma10OutReal[sma10OutNBElement.value - 1]
            },
            sma20Func = {
                // SMA 20
                val SMA20_TIME_PERIOD = 20
                val sma20OutBegIdx = MInteger()
                val sma20OutNBElement = MInteger()
                val sma20OutReal = DoubleArray(inReal.size)
                core.sma(0, inReal.size - 1, inReal, SMA20_TIME_PERIOD, sma20OutBegIdx, sma20OutNBElement, sma20OutReal)
                sma20OutReal[sma20OutNBElement.value - 1]
            },
            sma60Func = {
                // SMA 60
                val SMA60_TIME_PERIOD = 60
                val sma60OutBegIdx = MInteger()
                val sma60OutNBElement = MInteger()
                val sma60OutReal = DoubleArray(inReal.size)
                core.sma(0, inReal.size - 1, inReal, SMA60_TIME_PERIOD, sma60OutBegIdx, sma60OutNBElement, sma60OutReal)
                sma60OutReal[sma60OutNBElement.value - 1]
            },
            sma120Func = {
                // SMA 120
                val SMA120_TIME_PERIOD = 120
                val sma120OutBegIdx = MInteger()
                val sma120OutNBElement = MInteger()
                val sma120OutReal = DoubleArray(inReal.size)
                core.sma(
                    0,
                    inReal.size - 1,
                    inReal,
                    SMA120_TIME_PERIOD,
                    sma120OutBegIdx,
                    sma120OutNBElement,
                    sma120OutReal
                )
                sma120OutReal[sma120OutNBElement.value - 1]
            },
            sma200Func = {
                // SMA 200
                val SMA200_TIME_PERIOD = 200
                val sma200OutBegIdx = MInteger()
                val sma200OutNBElement = MInteger()
                val sma200OutReal = DoubleArray(inReal.size)
                core.sma(
                    0,
                    inReal.size - 1,
                    inReal,
                    SMA200_TIME_PERIOD,
                    sma200OutBegIdx,
                    sma200OutNBElement,
                    sma200OutReal
                )
                sma200OutReal[sma200OutNBElement.value - 1]
            },
            ema60Func = {
                // EMA 60
                val EMA60_TIME_PERIOD = 60
                val ema60OutBegIdx = MInteger()
                val ema60OutNBElement = MInteger()
                val ema60OutReal = DoubleArray(inReal.size)
                core.ema(0, inReal.size - 1, inReal, EMA60_TIME_PERIOD, ema60OutBegIdx, ema60OutNBElement, ema60OutReal)
                ema60OutReal[ema60OutNBElement.value - 1]
            },
        )
    }

    fun getObv(candles: List<ExchangeChartCandle>): Obv {
        // obv 계산
        val obvInReal = candles.map { it.closingPrice }.toDoubleArray()
        val obvInVolume = candles.map { it.accTradeVolume }.toDoubleArray()
        val obvOutBegIdx = MInteger()
        val obvOutNBElement = MInteger()
        val obvOutReal = DoubleArray(obvInReal.size)
        core.obv(0, obvInReal.size - 1, obvInReal, obvInVolume, obvOutBegIdx, obvOutNBElement, obvOutReal)

        // obv 시그널 계산
        val TIME_PERIOD = 30
        val signalOutBegIdx = MInteger()
        val signalOutNBElement = MInteger()
        val signalOutReal = DoubleArray(obvOutReal.size)
        core.sma(0, obvOutReal.size - 1, obvOutReal, TIME_PERIOD, signalOutBegIdx, signalOutNBElement, signalOutReal)

        return Obv(
            obv = obvOutReal[obvOutNBElement.value - 1],
            hist = obvOutReal[obvOutNBElement.value - 1] - signalOutReal[signalOutNBElement.value - 1],
        )
    }

    fun getBollingerBands(candles: List<ExchangeChartCandle>): BollingerBands {

        // 볼린저 밴드 계산
        val MA_TYPE = MAType.Sma
        val TIME_PERIOD = 20
        val NB_DEV_UP = 2
        val NB_DEV_DOWN = 2
        val bbandsInReal = candles.map { it.closingPrice }.toDoubleArray()
        val bbandsOutRealUpperBand = DoubleArray(bbandsInReal.size)
        val bbandsOutRealMiddleBand = DoubleArray(bbandsInReal.size)
        val bbandsOutRealLowerBand = DoubleArray(bbandsInReal.size)
        val bbandsOutBegIdx = MInteger()
        val bbandsOutNBElement = MInteger()
        core.bbands(
            0,
            bbandsInReal.size - 1,
            bbandsInReal,
            TIME_PERIOD,
            NB_DEV_UP.toDouble(),
            NB_DEV_DOWN.toDouble(),
            MA_TYPE,
            bbandsOutBegIdx,
            bbandsOutNBElement,
            bbandsOutRealUpperBand,
            bbandsOutRealMiddleBand,
            bbandsOutRealLowerBand
        )

        // 볼린저 밴드 높이 계산
        val bbandsOutRealHeight = DoubleArray(bbandsOutNBElement.value)
        for (i in bbandsOutRealHeight.indices) {
            bbandsOutRealHeight[i] = bbandsOutRealUpperBand[i] - bbandsOutRealLowerBand[i]
        }

        // 볼린저 밴드 높이 시그널 계산
        val SIGNAL_TIME_PERIOD = 10
        val signalOutBegIdx = MInteger()
        val signalOutNBElement = MInteger()
        val signalOutReal = DoubleArray(bbandsOutRealHeight.size)
        core.sma(
            0,
            bbandsOutRealHeight.size - 1,
            bbandsOutRealHeight,
            SIGNAL_TIME_PERIOD,
            signalOutBegIdx,
            signalOutNBElement,
            signalOutReal
        )
        return BollingerBands(
            upper = bbandsOutRealUpperBand[bbandsOutNBElement.value - 1],
            middle = bbandsOutRealMiddleBand[bbandsOutNBElement.value - 1],
            lower = bbandsOutRealLowerBand[bbandsOutNBElement.value - 1],
            height = bbandsOutRealHeight[bbandsOutNBElement.value - 1],
            heightHist = bbandsOutRealHeight[bbandsOutNBElement.value - 1] - signalOutReal[signalOutNBElement.value - 1],
        )
    }

    fun getMACD(candles: List<ExchangeChartCandle>): Macd {
        val FAST_PERIOD = 12
        val SLOW_PERIOD = 26
        val SIGNAL_PERIOD = 9
        val inReal = candles.map { it.closingPrice }.toDoubleArray()
        val outMACD = DoubleArray(inReal.size)
        val outMACDSignal = DoubleArray(inReal.size)
        val outMACDHist = DoubleArray(inReal.size)
        val outBegIdx = MInteger()
        val outNBElement = MInteger()
        core.macd(
            0,
            inReal.size - 1,
            inReal,
            FAST_PERIOD,
            SLOW_PERIOD,
            SIGNAL_PERIOD,
            outBegIdx,
            outNBElement,
            outMACD,
            outMACDSignal,
            outMACDHist
        )
        return Macd(
            hist = outMACDHist[outNBElement.value - 1],
            signal = outMACDSignal[outNBElement.value - 1],
            macd = outMACD[outNBElement.value - 1],
        )
    }

    // RSI(지수 가중 이동 평균)
    // https://www.investopedia.com/terms/r/rsi.asp
    // https://rebro.kr/139
    fun getRsi(candles: List<ExchangeChartCandle>): Double {
        val RSI_STANDARD_PERIOD = 14
        var U = 0.0
        var D = 0.0
        for (i in candles.indices) {
            if (!hasNext(i, candles.size)) {
                break
            }
            val yesterdayPrice = candles[i].closingPrice
            val todayPrice = candles[i + 1].closingPrice
            if (todayPrice > yesterdayPrice) {
                U += todayPrice - yesterdayPrice
            } else {
                D += yesterdayPrice - todayPrice
            }
        }
        var AU = U / RSI_STANDARD_PERIOD
        var AD = D / RSI_STANDARD_PERIOD
        for (i in candles.indices) {
            if (!hasNext(i, candles.size)) {
                break
            }
            val yesterdayPrice = candles[i].closingPrice
            val todayPrice = candles[i + 1].closingPrice
            if (yesterdayPrice < todayPrice) {
                AU = ((RSI_STANDARD_PERIOD - 1) * AU + todayPrice - yesterdayPrice) / RSI_STANDARD_PERIOD
                AD = ((RSI_STANDARD_PERIOD - 1) * AD + 0) / RSI_STANDARD_PERIOD
            } else {
                AU = ((RSI_STANDARD_PERIOD - 1) * AU + 0) / RSI_STANDARD_PERIOD
                AD = ((RSI_STANDARD_PERIOD - 1) * AD + yesterdayPrice - todayPrice) / RSI_STANDARD_PERIOD
            }
        }
        val RS = AU / AD
        return RS / (1 + RS)
    }

    private fun hasNext(index: Int, size: Int): Boolean {
        return index + 1 < size
    }
}