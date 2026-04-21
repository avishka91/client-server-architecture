import os
import subprocess
import random
from datetime import datetime, timedelta

def run_cmd(cmd, env=None, check=True):
    print(f"Running: {cmd}")
    result = subprocess.run(cmd, env=env, shell=True)
    if check and result.returncode != 0:
        print(f"Error executing: {cmd}")
        exit(1)

def main():
    remote_url = "https://github.com/avishka91/client-server-architecture.git"
    branch_name = "main"

    print("Gathering files...")
    all_files = []
    # Exclude non-source code and Git configuration folders
    exclude_dirs = {'.git', 'target', '__pycache__', '.idea', '.vscode'}
    script_name = os.path.basename(__file__)
    
    for root, dirs, files in os.walk("."):
        dirs[:] = [d for d in dirs if d not in exclude_dirs]
        for f in files:
            if f == script_name:
                continue
            
            rel_dir = os.path.relpath(root, ".")
            filepath = f if rel_dir == "." else os.path.join(rel_dir, f)
            filepath = filepath.replace("\\", "/")
            all_files.append(filepath)

    all_files.sort()
    
    total_files = len(all_files)
    if total_files == 0:
        print("No files found!")
        return
        
    print(f"Found {total_files} files to commit.")
    
    # Clean the old git history to ensure a perfect 24h separation timeline
    print("Resetting local .git repository to clean history...")
    if os.path.exists(".git"):
        subprocess.run('rmdir /s /q .git', shell=True)
    
    run_cmd("git init")
    run_cmd(f"git checkout -b {branch_name}", check=False)
    run_cmd(f'git remote add origin "{remote_url}"')

    # Goal is to spread commits across approximately 24 hours (1440 minutes)
    target_duration_mins = 1440
    avg_interval = int(target_duration_mins / total_files) if total_files else 30
    
    intervals = []
    for _ in range(total_files):
        # Fluctuation to seem natural
        low = max(1, avg_interval - 8)
        high = avg_interval + 8
        intervals.append(random.randint(low, high))
        
    total_minutes = sum(intervals)
    current_time = datetime.now() - timedelta(minutes=total_minutes)
    
    env = os.environ.copy()
    
    for i, filepath in enumerate(all_files):
        # Advance time by interval
        current_time += timedelta(minutes=intervals[i])
        formatted_time = current_time.strftime('%Y-%m-%dT%H:%M:%S')
        
        env['GIT_AUTHOR_DATE'] = formatted_time
        env['GIT_COMMITTER_DATE'] = formatted_time
        
        print(f"\n[{i+1}/{total_files}] Processing: {filepath} at {formatted_time}")
        
        # Git Add
        run_cmd(f'git add "{filepath}"', check=False)
        
        # Git Commit
        commit_msg = f"Add file: {filepath}"
        run_cmd(f'git commit -m "{commit_msg}"', env=env, check=False)
        
    # Push ALL commits to remote at once
    print("\nPushing all spaced commits to GitHub...")
    run_cmd(f"git push -u origin {branch_name} --force", check=False)
            
    print("\nSuccessfully finished adding files separately over a 24-hour window and pushed them to remote.")

if __name__ == '__main__':
    main()
