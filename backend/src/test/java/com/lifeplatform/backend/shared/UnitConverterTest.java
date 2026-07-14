package com.lifeplatform.backend.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnitConverterTest {

    private UnitConverter unitConverter;

    @BeforeEach
    void setUp() {
        unitConverter = new UnitConverter();
    }

    @Test
    void toBaseUnitConvertsAllSupportedUnitsCorrectly() {
        assertThat(unitConverter.toBaseUnit(500.0, "g")).isEqualTo(0.5);
        assertThat(unitConverter.toBaseUnit(250.0, "gr")).isEqualTo(0.25);
        assertThat(unitConverter.toBaseUnit(2.0, "kg")).isEqualTo(2.0);
        assertThat(unitConverter.toBaseUnit(750.0, "ml")).isEqualTo(0.75);
        assertThat(unitConverter.toBaseUnit(1.5, "l")).isEqualTo(1.5);
        assertThat(unitConverter.toBaseUnit(1.5, "lt")).isEqualTo(1.5);
    }

    @Test
    void fromBaseUnitConvertsAllSupportedUnitsCorrectly() {
        assertThat(unitConverter.fromBaseUnit(0.5, "g")).isEqualTo(500.0);
        assertThat(unitConverter.fromBaseUnit(0.25, "gr")).isEqualTo(250.0);
        assertThat(unitConverter.fromBaseUnit(2.0, "kg")).isEqualTo(2.0);
        assertThat(unitConverter.fromBaseUnit(0.75, "ml")).isEqualTo(750.0);
        assertThat(unitConverter.fromBaseUnit(1.5, "l")).isEqualTo(1.5);
        assertThat(unitConverter.fromBaseUnit(1.5, "lt")).isEqualTo(1.5);
    }

    @Test
    void toBaseUnitReturnsZeroWhenQuantityIsNullZeroOrNegative() {
        assertThat(unitConverter.toBaseUnit(null, "kg")).isEqualTo(0.0);
        assertThat(unitConverter.toBaseUnit(0.0, "kg")).isEqualTo(0.0);
        assertThat(unitConverter.toBaseUnit(-1.0, "kg")).isEqualTo(0.0);
    }

    @Test
    void fromBaseUnitReturnsZeroWhenBaseQuantityIsZeroOrNegative() {
        assertThat(unitConverter.fromBaseUnit(0.0, "kg")).isEqualTo(0.0);
        assertThat(unitConverter.fromBaseUnit(-1.0, "kg")).isEqualTo(0.0);
    }

    @Test
    void currentBehaviorUnknownUnitReturnsQuantityWithoutConversion() {
        assertThat(unitConverter.toBaseUnit(123.45, "onza")).isEqualTo(123.45);
        assertThat(unitConverter.fromBaseUnit(123.45, "onza")).isEqualTo(123.45);
    }
}
