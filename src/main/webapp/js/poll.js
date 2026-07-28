(function () {
  "use strict";

  var POLL_INTERVAL_MS = 4000;

  function setupCandidateAdder() {
    var addButton = document.getElementById("add-candidate-btn");
    var list = document.getElementById("candidate-list");
    if (!addButton || !list) {
      return;
    }

    addButton.addEventListener("click", function () {
      var row = document.createElement("div");
      row.className = "form-row";
      row.innerHTML = '<div class="form-group grow"><input class="input" type="datetime-local" name="candidateDateTimes"></div>';
      list.appendChild(row);
    });
  }

  function setupVoteMatrix() {
    var config = window.POLL_CONFIG;
    if (!config) {
      return;
    }

    function refreshStatus() {
      fetch("/polls/" + config.pollId + "/status")
          .then(function (response) { return response.json(); })
          .then(function (responses) {
            document.querySelectorAll("[data-cell-candidate]").forEach(function (cell) {
              cell.textContent = "－";
              cell.removeAttribute("title");
            });

            var availableCounts = {};

            responses.forEach(function (item) {
              var selector = '[data-cell-candidate="' + item.candidateId + '"][data-cell-user="' + item.userId + '"]';
              var cell = document.querySelector(selector);
              if (cell) {
                cell.textContent = item.answerSymbol;
                if (item.comment) {
                  cell.title = item.comment;
                }
              }
              if (item.answer === "AVAILABLE") {
                availableCounts[item.candidateId] = (availableCounts[item.candidateId] || 0) + 1;
              }
            });

            document.querySelectorAll("[data-candidate-row]").forEach(function (row) {
              row.classList.remove("is-best-candidate");
            });

            var bestCandidateId = null;
            var bestCount = 0;
            Object.keys(availableCounts).forEach(function (candidateId) {
              if (availableCounts[candidateId] > bestCount) {
                bestCount = availableCounts[candidateId];
                bestCandidateId = candidateId;
              }
            });

            if (bestCandidateId) {
              var bestRow = document.querySelector('[data-candidate-row="' + bestCandidateId + '"]');
              if (bestRow) {
                bestRow.classList.add("is-best-candidate");
              }
            }
          });
    }

    document.querySelectorAll("[data-vote-buttons]").forEach(function (container) {
      var candidateId = container.getAttribute("data-vote-buttons");

      container.querySelectorAll("[data-answer]").forEach(function (button) {
        button.addEventListener("click", function () {
          container.querySelectorAll("[data-answer]").forEach(function (b) { b.classList.remove("btn-primary"); });
          button.classList.add("btn-primary");

          var commentInput = document.querySelector('[data-comment-input="' + candidateId + '"]');
          var comment = commentInput ? commentInput.value : "";

          fetch("/polls/" + config.pollId + "/vote", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
              candidateId: candidateId,
              answer: button.getAttribute("data-answer"),
              comment: comment
            })
          }).then(refreshStatus);
        });
      });
    });

    refreshStatus();

    if (config.pollStatus === "OPEN") {
      setInterval(refreshStatus, POLL_INTERVAL_MS);
    }
  }

  document.addEventListener("DOMContentLoaded", function () {
    setupCandidateAdder();
    setupVoteMatrix();
  });
})();
