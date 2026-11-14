"""Offline regression tests; never invoke gh or mutate GitHub."""
import importlib.util
import os
from pathlib import Path
import unittest
from unittest.mock import patch


class CloseResolvedIssuesTest(unittest.TestCase):
    def setUp(self):
        script = Path(__file__).resolve().parents[1] / "close_resolved_issues.py"
        spec = importlib.util.spec_from_file_location("close_resolved_issues", script)
        self.module = importlib.util.module_from_spec(spec)
        with patch.dict(os.environ, {
            "GITHUB_REPOSITORY": "test-owner/fork",
            "UPSTREAM_REPOSITORY": "test-owner/upstream",
        }):
            spec.loader.exec_module(self.module)

    def run_case(self, states, checklist="- [x] Done", comment_pr=False):
        links = "\n".join(
            f"https://github.com/test-owner/upstream/pull/{pr}" for pr in states
        )
        body = checklist if comment_pr else checklist + "\n" + links

        def gh_json(*args):
            if args[:2] == ("issue", "list"):
                return [{"number": 7, "body": body}]
            if args[:2] == ("issue", "view"):
                return [{"body": links}] if comment_pr else []
            if args[:2] == ("pr", "view"):
                return {"state": states[int(args[2])]}
            self.fail(f"Unexpected gh call: {args}")

        with patch.object(self.module, "gh_json", side_effect=gh_json), \
                patch.object(self.module.subprocess, "run") as close:
            self.module.main()
            return close.call_args_list

    def test_all_merged_closes_with_all_pr_references(self):
        calls = self.run_case({101: "MERGED", 102: "MERGED"})
        self.assertEqual(len(calls), 1)
        self.assertEqual(calls[0].args[0][:3], ["gh", "issue", "close"])
        self.assertIn("#101, #102", calls[0].args[0][-1])

    def test_closed_and_merged_does_not_close(self):
        self.assertEqual(self.run_case({101: "CLOSED", 102: "MERGED"}), [])

    def test_open_and_merged_does_not_close(self):
        self.assertEqual(self.run_case({101: "OPEN", 102: "MERGED"}), [])

    def test_only_closed_does_not_close(self):
        self.assertEqual(self.run_case({101: "CLOSED"}), [])

    def test_unchecked_task_does_not_close(self):
        self.assertEqual(self.run_case({101: "MERGED"}, "- [ ] Remaining"), [])

    def test_no_pr_links_does_not_close(self):
        self.assertEqual(self.run_case({}), [])

    def test_pr_link_in_comment_still_blocks_when_unmerged(self):
        self.assertEqual(self.run_case({101: "CLOSED", 102: "MERGED"}, comment_pr=True), [])


if __name__ == "__main__":
    unittest.main()
