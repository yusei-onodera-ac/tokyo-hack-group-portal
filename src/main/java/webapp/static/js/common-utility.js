/**
 * REST API 通信を共通化するユーティリティモジュール。
 */
const ApiClient = {
    /**
     * JSONデータを取得する共通GET関数
     * @param {string} requestUrl 取得先URL
     * @returns {Promise<any>} レスポンスオブジェクト
     */
    async fetchJsonData(requestUrl) {
        try {
            const apiResponse = await fetch(requestUrl);
            if (!apiResponse.ok) {
                throw new Error(`HTTPエラー Status: ${apiResponse.status}`);
            }
            return await apiResponse.json();
        } catch (networkError) {
            console.error("API通信に失敗しました:", networkError);
            throw networkError;
        }
    }
};