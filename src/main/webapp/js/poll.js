(function () {
  "use strict";

  var POLL_INTERVAL_MS = 4000;
  var ANSWER_LABELS = { AVAILABLE: "○", MAYBE: "△", UNAVAILABLE: "×" };

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

    function applyOwnResponse(candidateId, answer, comment) {
      var container = document.querySelector('[data-vote-buttons="' + candidateId + '"]');
      if (container) {
        container.querySelectorAll("[data-answer]").forEach(function (button) {
          button.classList.toggle("btn-primary", button.getAttribute("data-answer") === answer);
          button.classList.toggle("btn-secondary", button.getAttribute("data-answer") !== answer);
        });
      }
      var commentInput = document.querySelector('[data-comment-input="' + candidateId + '"]');
      if (commentInput && document.activeElement !== commentInput) {
        commentInput.value = comment || "";
      }
    }

    function refreshStatus() {
      fetch("/polls/" + config.pollId + "/status")
          .then(function (response) { return response.json(); })
          .then(function (responses) {
            document.querySelectorAll("[data-cell-candidate]").forEach(function (cell) {
              cell.textContent = "－";
              cell.removeAttribute("title");
            });

            var countsByCandidate = {};

            responses.forEach(function (item) {
              var selector = '[data-cell-candidate="' + item.candidateId + '"][data-cell-user="' + item.userId + '"]';
              var cell = document.querySelector(selector);
              if (cell) {
                cell.textContent = item.answerSymbol;
                if (item.comment) {
                  cell.title = item.comment;
                }
              }

              if (!countsByCandidate[item.candidateId]) {
                countsByCandidate[item.candidateId] = { AVAILABLE: 0, MAYBE: 0, UNAVAILABLE: 0 };
              }
              countsByCandidate[item.candidateId][item.answer] += 1;

              if (config.currentUserId && String(item.userId) === String(config.currentUserId)) {
                applyOwnResponse(item.candidateId, item.answer, item.comment);
              }
            });

            document.querySelectorAll("[data-candidate-row]").forEach(function (row) {
              row.classList.remove("is-best-candidate");
            });

            var bestCandidateId = null;
            var bestCount = 0;

            Object.keys(countsByCandidate).forEach(function (candidateId) {
              var counts = countsByCandidate[candidateId];
              var countCell = document.querySelector('[data-count-candidate="' + candidateId + '"]');
              if (countCell) {
                countCell.textContent = ANSWER_LABELS.AVAILABLE + counts.AVAILABLE + " "
                    + ANSWER_LABELS.MAYBE + counts.MAYBE + " "
                    + ANSWER_LABELS.UNAVAILABLE + counts.UNAVAILABLE;
              }
              if (counts.AVAILABLE > bestCount) {
                bestCount = counts.AVAILABLE;
                bestCandidateId = candidateId;
              }
            });

            document.querySelectorAll("[data-count-candidate]").forEach(function (cell) {
              if (cell.textContent === "集計中…") {
                cell.textContent = ANSWER_LABELS.AVAILABLE + "0 " + ANSWER_LABELS.MAYBE + "0 " + ANSWER_LABELS.UNAVAILABLE + "0";
              }
            });

            if (bestCandidateId && bestCount > 0) {
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
          var commentInput = document.querySelector('[data-comment-input="' + candidateId + '"]');
          var comment = commentInput ? commentInput.value : "";
          var answer = button.getAttribute("data-answer");

          applyOwnResponse(candidateId, answer, comment);

          fetch("/polls/" + config.pollId + "/vote", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
              candidateId: candidateId,
              answer: answer,
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
