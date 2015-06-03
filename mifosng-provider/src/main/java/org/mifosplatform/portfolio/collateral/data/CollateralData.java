/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.collateral.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

/**
 * Immutable data object for Collateral data.
 */
public class CollateralData {

    private final Long id;
    private final CodeValueData type;
    private final CodeValueData goldfine;
    private final CodeValueData jewellery;
    private final CodeValueData maketwo;
    private final BigDecimal value;
    private final BigDecimal actualcost;
    private BigDecimal gross;
    private BigDecimal impurity;
    private BigDecimal net;
    private BigDecimal stone;
    private final String description;
    @SuppressWarnings("unused")
    private final Collection<CodeValueData> allowedCollateralTypes;
    private final Collection<CodeValueData>allowedGoldfineTypes;
    private final Collection<CodeValueData>allowedJewelleryTypes;
    private final Collection<CodeValueData>allowedMakeTwoTypes;
    private final CurrencyData currency;

    public static CollateralData instance(final Long id, final CodeValueData type,final CodeValueData goldfine, final CodeValueData jewellery,final CodeValueData maketwo,final BigDecimal value,final BigDecimal actualcost,final BigDecimal gross,final BigDecimal impurity,final BigDecimal net, final BigDecimal stone, final String description,
            final CurrencyData currencyData) {
        return new CollateralData(id, type,goldfine,jewellery,maketwo, value,actualcost,gross,impurity,net,stone, description, currencyData);
    }

    public static CollateralData template(final Collection<CodeValueData> codeValues, final Collection<CodeValueData> goldfineValues, final Collection<CodeValueData> jewelleryValues ,final Collection<CodeValueData> maketwoValues) {
        return new CollateralData(null, null, null, null, null,null,null,null,null,null,null,null,null, codeValues,goldfineValues,jewelleryValues,maketwoValues);
    }

    private CollateralData(final Long id, final CodeValueData type,final CodeValueData goldfine, final CodeValueData jewellery,final CodeValueData maketwo, final BigDecimal value,final BigDecimal actualcost,final BigDecimal gross,final BigDecimal impurity, final BigDecimal net, final BigDecimal stone, final String description,
            final CurrencyData currencyData ) {
        this.id = id;
        this.type = type;
        this.goldfine = goldfine;
        this.jewellery = jewellery;
        this.maketwo = maketwo;
        this.value = value;
        this.actualcost = actualcost;
        this.gross = gross;
        this.impurity = impurity;
        this.net = net;
        this.stone = stone;
        this.description = description;
        this.currency = currencyData;
        this.allowedCollateralTypes = null;
        this.allowedGoldfineTypes = null;
        this.allowedJewelleryTypes = null;
        this.allowedMakeTwoTypes = null;
    }

    private CollateralData(final Long id, final CodeValueData type,final CodeValueData goldfine,final CodeValueData jewellery,final CodeValueData maketwo, final BigDecimal value,final BigDecimal actualcost,final BigDecimal gross, final BigDecimal impurity, final BigDecimal net, final BigDecimal stone,final String description,
            final CurrencyData currencyData,final Collection<CodeValueData> allowedCollateralTypes,final Collection<CodeValueData> allowedGoldfineTypes, final Collection<CodeValueData>allowedJewelleryTypes, final Collection<CodeValueData>allowedMakeTwoTypes) {
        this.id = id;
        this.type = type;
        this.goldfine = goldfine;
        this.jewellery = jewellery;
        this.maketwo = maketwo;
        this.value = value;
        this.actualcost = actualcost;
        this.gross = gross;
        this.impurity = impurity;
        this.net = net;
        this.stone = stone;
        this.description = description;
        this.currency = currencyData;
        this.allowedCollateralTypes = allowedCollateralTypes;
        this.allowedGoldfineTypes = allowedGoldfineTypes;
        this.allowedJewelleryTypes = allowedJewelleryTypes;
        this.allowedMakeTwoTypes = allowedMakeTwoTypes;
    }

    public CollateralData template(final CollateralData collateralData, final Collection<CodeValueData> codeValues,final Collection<CodeValueData> goldfineValues, final Collection<CodeValueData> jewelleryValues, final Collection<CodeValueData> maketwoValues) {
        return new CollateralData(collateralData.id, collateralData.type,collateralData.goldfine, collateralData.jewellery,collateralData.maketwo,collateralData.value,collateralData.actualcost, collateralData.gross,collateralData.impurity, collateralData.net,collateralData.stone,collateralData.description,
                collateralData.currency, codeValues,goldfineValues, jewelleryValues, maketwoValues );
    }

	
}