package dbfit.util;

import java.math.BigDecimal;

public class NormalisedBigDecimal extends BigDecimal {

    /**
     * generated ID
     */
    private static final long serialVersionUID = 2431086524011308737L;

    public NormalisedBigDecimal(final BigDecimal bd) {
        super(bd.unscaledValue(), bd.scale());
    }

    @Override
    public boolean equals(final Object o2) {
        return (o2 != null) && (0 == compareTo((BigDecimal) o2));
    }
}
