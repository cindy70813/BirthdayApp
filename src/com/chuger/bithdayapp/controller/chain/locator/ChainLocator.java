package com.chuger.bithdayapp.controller.chain.locator;

import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.chuger.bithdayapp.controller.chain.chain.impl.FbChain;
import com.chuger.bithdayapp.controller.chain.chain.impl.GoogleChain;
import com.chuger.bithdayapp.controller.chain.chain.impl.VkChain;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Acer5740
 * Date: 26.02.12
 * Time: 3:20
 */
public class ChainLocator {
    public static final String VK_CHAIN_ALIAS = "VK_CHAIN_ALIAS";
    public static final String FB_CHAIN_ALIAS = "FB_CHAIN_ALIAS";
    public static final String GOOGLE_CHAIN_ALIAS = "GOOGLE_CHAIN_ALIAS";

    private final static Map<String, Chain> CHAIN_MAP = new HashMap<String, Chain>() {
        {
            put(VK_CHAIN_ALIAS, new VkChain());
            put(FB_CHAIN_ALIAS, new FbChain());
            put(GOOGLE_CHAIN_ALIAS, new GoogleChain());
        }
    };

    public static Chain getChain(final String chainAlias) {
        return CHAIN_MAP.containsKey(chainAlias) ? CHAIN_MAP.get(chainAlias) : null;
    }
}
