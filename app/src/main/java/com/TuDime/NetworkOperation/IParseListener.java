package com.TuDime.NetworkOperation;

/**
 * Created by KRISHNENDU MANNA on 28/6/19.
 */

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Interface for responses
 *
 * @author DearDhruv
 */
public interface IParseListener {
// Interface methods for Responses
    /**
     * Invoked when any network failure or JSON parsing failure.
     *
     * @param error
     * @param requestCode
     */
    void ErrorResponse(VolleyError error, int requestCode);
    /**
     * Invoked when successful response and successful JSON parsing completed.
     *
     * @param response
     * @param requestCode
     */
    void SuccessResponse(JSONObject response, int requestCode);
}