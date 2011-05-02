import org.openquant.backtest.Position
import org.openquant.backtest.Series

def Close = context.closeSeries()
def EMAClose = Close.EMA(12)
def RSI = Close.RSI(25)

for (int bar = 25; bar < context.barsCount() - 1; bar++) {
	
	println "Date[${context.date(bar)}], Close[${Close.getAt(bar)}], " +
	"EMA[${EMAClose.getAt(bar)}], RSI[${RSI.getAt(bar)}]"
	
	Position pos = context.getLastOpenPosition();	
	
	if (RSI.getAt(bar - 1) > RSI.getAt(bar - 2)) {
		context.buyAtLimit(bar, context.close(bar), 1000, "ELL")
	}
	else{
		if (context.hasOpenPositions()) {
			context.sellAtLimit(bar, pos, pos.getEntryPrice() * 1.05, "XLL")
		}
	
		if (context.hasOpenPositions()) {
			context.sellAtStop(bar, pos, pos.getEntryPrice() * 0.95, "XLS")
		}
	}	
}